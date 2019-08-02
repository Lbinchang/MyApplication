package com.tiza.pub.rp.support.protocol.cmd;

import com.tiza.pub.air.model.Header;
import com.tiza.pub.rp.support.protocol.Gb6DataProcess;
import org.springframework.stereotype.Service;

/**
 * Description: Gb6_05
 * Author: DIYILIU
 * Update: 2019-07-03 09:51
 */

@Service
public class Gb6_05 extends Gb6DataProcess {

    public Gb6_05() {
        this.cmdId = 0x05;
    }

    @Override
    public void parse(byte[] content, Header header) {


    }
}
