package com.tiza.pub.air.model;

import lombok.Data;

/**
 * Description: KafkaMsg
 * Author: DIYILIU
 * Update: 2019-06-27 19:52
 */

@Data
public class KafkaMsg<T> {
    private String topic;

    private String key;

    private T value;

    public KafkaMsg() {

    }

    public KafkaMsg(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public KafkaMsg(String topic, String key, T value) {
        this.topic = topic;
        this.key = key;
        this.value = value;
    }
}
