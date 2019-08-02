package com.tiza.pub.air.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Description: Gb6Header
 * Author: DIYILIU
 * Update: 2019-06-06 16:13
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class Gb6Header extends Header{
    private String vehicle;

    private int version;
    private int encrypt;
    private int length;

    private int check;
}
