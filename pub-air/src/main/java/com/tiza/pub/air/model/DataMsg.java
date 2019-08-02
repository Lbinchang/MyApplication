package com.tiza.pub.air.model;

import lombok.Data;

/**
 * Description: DataMsg
 * Author: DIYILIU
 * Update: 2019-07-03 09:38
 */

@Data
public class DataMsg {

    private String terminal;

    private Integer cmd;

    private Long time;

    private Integer flow;

    private String data;
}
