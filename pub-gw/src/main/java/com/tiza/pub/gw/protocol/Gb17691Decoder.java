package com.tiza.pub.gw.protocol;

import com.tiza.pub.gw.netty.common.BaseDecoder;
import com.tiza.pub.air.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Description: Gb17691Decoder
 * Author: DIYILIU
 * Update: 2019-06-27 19:29
 */

@Slf4j
public class Gb17691Decoder extends BaseDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list)  {
        if (byteBuf.readableBytes() < 25) {
            return;
        }

        byteBuf.markReaderIndex();
        byte header1 = byteBuf.readByte();
        byte header2 = byteBuf.readByte();

        // 头标识
        if (header1 != 0x23 || header2 != 0x23) {
            String host = channelHandlerContext.channel().remoteAddress().toString().trim().replaceFirst("/", "");
            log.error("协议头校验失败, 断开连接[{}]!", host);
            return;
        }

        byteBuf.readBytes(new byte[20]);
        // 数据单元长度
        int length = byteBuf.readUnsignedShort();
        if (byteBuf.readableBytes() < length + 1) {

            byteBuf.resetReaderIndex();
            return;
        }
        byteBuf.resetReaderIndex();

        byte[] bytes = new byte[2 + 20 + 2 + length + 1];
        byteBuf.readBytes(bytes);



        // 验证校验位
        if (!check(bytes)) {
            log.error("校验位错误, 断开连接!");
            return;
        }

        list.add(Unpooled.copiedBuffer(bytes));
    }

    /**
     * 验证校验位
     *
     * @param bytes
     * @return
     */
    private boolean check(byte[] bytes) {
        int length = bytes.length;

        byte last = bytes[length - 1];
        byte check = CommonUtil.checkCode(bytes, 2, length - 3);

        if (last == check) {
            return true;
        }
        log.error("校验位错误, [实际值: {}, 计算值: {}, 原始指令: {}]!", last, check, CommonUtil.bytesToStr(bytes));
        return false;
    }
}
