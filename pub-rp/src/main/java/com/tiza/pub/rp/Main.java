package com.tiza.pub.rp;


import com.google.common.collect.Maps;
import com.tiza.pub.rp.support.config.CliParser;
import com.tiza.pub.rp.support.config.Config1;
import com.tiza.pub.rp.support.config.ConsumerConfig;
import com.tiza.pub.rp.support.config.ProducerConfig;

import java.util.Map;
import java.util.Properties;

/**
 * Description: Main
 * Author: DIYILIU
 * Update: 2019-07-03 09:33
 */
public class Main {

    public static void main(String[] args) throws Exception{

        CliParser parser = new CliParser.Builder().build();
        parser.parse(args);

      /*  StringBuilder stringBuilder2=new StringBuilder( parser.configPath());
        if(args.length!=0) {
            int index = stringBuilder2.indexOf(".properties");
            StringBuilder stringBuilder=new StringBuilder();
            for(int i=0;i<args.length;i++) {
               stringBuilder.append(args[i]);
            }
            stringBuilder2.insert(index, stringBuilder);
        }
        String path = stringBuilder2.toString();
        Properties properties = Config1.read(path, parser.profile());*/


      // 上述方法可行，Config1 中有转换参数的方法，所以采用原方法，
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<args.length;i++) {
            stringBuilder.append(args[i]);
        }


        Properties properties = Config1.read(parser.configPath(),stringBuilder.toString());

        //初始化生产者和消费者，跟之前kafka 0.9之前的版本不同的是，每次注册都要进行初始化操作

        Map<String,String> config_consumer = Maps.newHashMap();
        config_consumer.put("kafka.brokers",properties.getProperty("kafka.brokers"));
        config_consumer.put("kafka.raw-topic",properties.getProperty("kafka.raw-topic"));
        config_consumer.put("kafka.groupId",properties.getProperty("kafka.groupId"));

        Map<String,String> config_producer = Maps.newHashMap();
        config_producer.put("kafka.brokers",properties.getProperty("kafka.brokers"));
        config_producer.put("kafka.work-topic",properties.getProperty("kafka.work-topic"));


        ProducerConfig producerConfig = new ProducerConfig(config_producer);
        producerConfig.initProducer();
        ConsumerConfig consumerConfig = new ConsumerConfig(config_consumer);
        consumerConfig.consume();

    }
}
