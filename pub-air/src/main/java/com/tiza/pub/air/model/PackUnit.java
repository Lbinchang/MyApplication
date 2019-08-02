package com.tiza.pub.air.model;

import lombok.Data;

import java.util.Map;

/**
 * Description: PackUnit
 * Author: DIYILIU
 * Update: 2019-06-14 11:19
 */

@Data
public class PackUnit {

    public PackUnit(int id) {
        this.id = id;
    }

    private int id;

    private Map<String, Object> data;
}
