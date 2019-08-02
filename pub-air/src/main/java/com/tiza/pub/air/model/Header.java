package com.tiza.pub.air.model;

import lombok.Data;

/**
 * Description: Header
 * Author: Wangw
 * Update: 2017-09-06 16:48
 */

@Data
public class Header {

    private String device;

    private Integer cmd;

    private Long time;

    private byte[] bytes;
}
