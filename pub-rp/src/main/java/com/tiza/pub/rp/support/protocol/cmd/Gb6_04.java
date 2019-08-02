package com.tiza.pub.rp.support.protocol.cmd;

import com.tiza.pub.air.model.Gb6Header;
import com.tiza.pub.air.model.Header;
import com.tiza.pub.rp.support.protocol.Gb6DataProcess;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.stereotype.Service;

/**
 * Description: Gb6_04
 * Author: DIYILIU
 * Update: 2019-07-03 09:51
 */

@Service
public class Gb6_04 extends Gb6DataProcess {

    public Gb6_04() {
        this.cmdId = 0x04;
    }

    @Override
    public void parse(byte[] content, Header header) {
        Gb6Header sixHeader = (Gb6Header) header;
        ByteBuf buf = Unpooled.copiedBuffer(content);
        // 解析时间
        fetchDate(sixHeader, buf);

        // 登入流水号
        int serial = buf.readUnsignedShort();
    }
}
