package com.tiza.pub.gw.netty.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Description: BaseEncoder
 * Author: DIYILIU
 * Update: 2019-06-27 19:22
 */
public class BaseEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) {
        if (o == null) {
            return;
        }

        ByteBuf buf = (ByteBuf) o;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        byteBuf.writeBytes(bytes);
    }
}
