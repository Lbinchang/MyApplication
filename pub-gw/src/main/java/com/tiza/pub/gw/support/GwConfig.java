package com.tiza.pub.gw.support;

import com.tiza.pub.gw.netty.GwServer;
import com.tiza.pub.air.entry.KafkaUtil;
import com.tiza.pub.air.util.SpringUtil;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.util.Properties;

/**
 * Description: GwConfig
 * Author: DIYILIU
 * Update: 2019-06-28 09:29
 */

@Configuration
@PropertySource("classpath:config.properties")
public class GwConfig {

    @Resource
    private Environment environment;


    @Bean(initMethod = "init")
    public GwServer server() {
        int port = environment.getProperty("gw-port", Integer.class);
        GwServer server = new GwServer();
        server.setPort(port);

        // 初始化 Kafka
        kafkaInit();
        return server;
    }

    public void kafkaInit() {
        String brokerList = environment.getProperty("kafka.broker-list");
        String topic1 = environment.getProperty("kafka.raw-topic");
        String topic2 = environment.getProperty("kafka.raw-topic-down");

        Properties props = new Properties();

        props.put("bootstrap.servers",brokerList);
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
  /*      props.put("metadata.broker.list", brokerList);

        // 消息传递到broker时的序列化方式
        props.put("key.serializer.class", "kafka.serializer.StringEncoder");
        props.put("serializer.class", "kafka.serializer.StringEncoder");*/
        // acks = 0：表示producer无需等待server端的应答消息
        // acks = 1：表示接收该消息记录的分区leader将消息记录写入到本地log文件，就返回Acknowledgement，告知producer本次发送已完成，而无需等待其他follower分区的确认。
        // acks = all：表示消息记录只有得到分区leader以及其他分区副本同步结点队列（ISR）中的分区follower的确认之后，才能回复acknowlegement，告知producer本次发送已完成。
        // acks = -1：等同于acks = all。
        props.put("request.required.acks", "1");
        // sync：同步(来一条数据提交一条不缓存), 默认 async：异步
        props.put("producer.type", "sync");
        // 重试次数
        props.put("message.send.max.retries", "3");

//        Producer<String, String> producer = new Producer(new ProducerConfig(props));
        Producer<String,String> producer = new KafkaProducer<String, String>(props);
        KafkaUtil.getInstance().init(producer, topic1, topic2);
    }

    /**
     * spring 工具类
     *
     * @return
     */
    @Bean
    public SpringUtil springUtil() {

        return new SpringUtil();
    }
}
