package com.tiza.pub.gw;

import com.tiza.pub.gw.support.GwConfig;
import com.tiza.pub.air.util.SpringUtil;

/**
 * Description: Main
 * Author: DIYILIU
 * Update: 2019-06-26 10:14
 */
public class Main {

    public static void main(String[] args) {

        SpringUtil.init(GwConfig.class);
    }
}
