package com.tiza.pub.rp.support.protocol.cmd;

import com.tiza.pub.air.model.Header;
import com.tiza.pub.air.model.IDataProcess;
import com.tiza.pub.rp.support.protocol.Gb6DataProcess;
import org.springframework.stereotype.Service;

/**
 * Description: Gb6_03
 * Author: DIYILIU
 * Update: 2019-07-03 09:51
 */

@Service
public class Gb6_03 extends Gb6DataProcess {

    public Gb6_03() {
        this.cmdId = 0x03;
    }

    @Override
    public void parse(byte[] content, Header header) {
        IDataProcess process = (IDataProcess) cmdCacheProvider.get(0x02);
        process.parse(content, header);
    }
}
