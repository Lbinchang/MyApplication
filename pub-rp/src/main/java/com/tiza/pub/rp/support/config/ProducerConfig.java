package com.tiza.pub.rp.support.config;

import com.tiza.pub.air.entry.KafkaUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.util.Map;
import java.util.Properties;

public class ProducerConfig {

    Producer<String,String> producer;
    String topic1;
    String topic2 = null;

    public ProducerConfig(Map<String,String> params){
        //写入kafka
        topic1 =params.get("kafka.work-topic");
        String topic2 = null;
        Properties props = new Properties();

        props.put("bootstrap.servers",params.get("kafka.brokers"));
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
//            props.put("metadata.broker.list", brokerList);

        // 消息传递到broker时的序列化方式

        // acks = 0：表示producer无需等待server端的应答消息
        // acks = 1：表示接收该消息记录的分区leader将消息记录写入到本地log文件，就返回Acknowledgement，告知producer本次发送已完成，而无需等待其他follower分区的确认。
        // acks = all：表示消息记录只有得到分区leader以及其他分区副本同步结点队列（ISR）中的分区follower的确认之后，才能回复acknowlegement，告知producer本次发送已完成。
        // acks = -1：等同于acks = all。
        props.put("request.required.acks", "1");
        // sync：同步(来一条数据提交一条不缓存), 默认 async：异步
        props.put("producer.type", "sync");
        // 重试次数
        props.put("message.send.max.retries", "3");

        producer = new KafkaProducer<String, String>(props);



    }
    //初始化kafka设置
    public void initProducer(){
        KafkaUtil.getInstance().init(producer, topic1, topic2);

    }



}
