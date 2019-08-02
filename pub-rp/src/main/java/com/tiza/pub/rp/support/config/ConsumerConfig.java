
package com.tiza.pub.rp.support.config;


import com.tiza.pub.air.cache.ICache;
import com.tiza.pub.air.model.DataMsg;
import com.tiza.pub.air.model.Gb6Header;
import com.tiza.pub.air.util.CommonUtil;
import com.tiza.pub.air.util.JacksonUtil;
import com.tiza.pub.air.util.SpringUtil;
import com.tiza.pub.rp.support.protocol.Gb6DataProcess;
import com.tiza.pub.rp.support.task.VehicleInfoTask;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;


import java.io.IOException;
import java.util.Arrays;
;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class ConsumerConfig  {
    private static KafkaConsumer<String, String> consumer;
    String TOPIC;

    public ConsumerConfig(Map<String,String> params){
        try {

        TOPIC = params.get("kafka.raw-topic");
        Properties props = new Properties();
//        props.put("bootstrap.servers", "master01:9092,master02:9092,slave01:9092");
        props.put("bootstrap.servers", params.get("kafka.brokers"));
        //每个消费者分配独立的组号
        props.put("group.id", params.get("kafka.groupId"));
        //如果value合法，则自动提交偏移量
        props.put("enable.auto.commit", "true");
        //设置多久一次更新被消费消息的偏移量
        props.put("auto.commit.interval.ms", "1000");
        //设置会话响应的时间，超过这个时间kafka可以选择放弃消费或者消费下一条消息
        props.put("session.timeout.ms", "30000");
        //自动重置offset
        props.put("auto.offset.reset","earliest");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<String, String>(props);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public  void consume(){

        consumer.subscribe(Arrays.asList(TOPIC));
        SpringUtil.init();

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for (ConsumerRecord<String, String> record : records){

                String kafkaMsg = record.value();
                try {
                    DataMsg msg = JacksonUtil.toObject(kafkaMsg, DataMsg.class);

                    String device = msg.getTerminal();

                    if (!VehicleInfoTask.vehicleMap.containsKey(device)) {
                        log.warn("车辆[{}]信息不存在!", device);
                        continue;
                    }
                    String vehicle = VehicleInfoTask.vehicleMap.get(device);

                    int cmd = msg.getCmd();
                    String data = msg.getData();
                    byte[] bytes = CommonUtil.hexStringToBytes(data);

                    ICache cmdCacheProvider = SpringUtil.getBean("cmdCacheProvider");
                    Gb6DataProcess process = (Gb6DataProcess) cmdCacheProvider.get(cmd);
                    if (process != null) {
                        Gb6Header header = (Gb6Header) process.parseHeader(bytes);
                        header.setVehicle(vehicle);
                        header.setTime(msg.getTime());

                        process.parse(header.getBytes(), header);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.printf("offset = %d, key = %s, value = %s",record.offset(), record.key(), record.value());
                System.out.println();
            }
        }
    }
}
