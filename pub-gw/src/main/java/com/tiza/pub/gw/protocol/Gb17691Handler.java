package com.tiza.pub.gw.protocol;

import com.tiza.pub.gw.netty.common.BaseHandler;
import com.tiza.pub.air.util.CommonUtil;
import com.tiza.pub.air.entry.KafkaUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

/**
 * Description: Gb17691Handler
 * Author: DIYILIU
 * Update: 2019-06-27 19:40
 */
public class Gb17691Handler extends BaseHandler {

    /**
     * 0: 平台转发数据; 1: 车载终端数据
     **/
    private final static int ORIGIN_FROM = 1;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0, bytes);

        // 协议头
        byteBuf.readShort();
        // 命令标识
        int cmd = byteBuf.readUnsignedByte();
   /*     // 应答标识
        int resp = byteBuf.readUnsignedByte();*/
        // VIN码
        byte[] vinArray = new byte[17];
        byteBuf.readBytes(vinArray);
        String vin = new String(vinArray).trim();

        // 终端版本号
        byteBuf.readByte();

        // 加密方式
        byteBuf.readByte();
        // 数据单元长度
        int length = byteBuf.readUnsignedShort();
        // 指令内容
        byte[] content = new byte[length];
        byteBuf.readBytes(content);

        // 记录上行
        KafkaUtil.send(vin, cmd, CommonUtil.bytesToStr(bytes), 1);
       /* // 需要应答
        if (resp == 0xFE) {
            doResponse(ctx, vin, cmd, content);
        }*/
    }

    /**
     * 指令应答
     *
     * @param ctx
     * @param terminal
     * @param cmd
     * @param content
     */
    private void doResponse(ChannelHandlerContext ctx, String terminal, int cmd, byte[] content) {
        byte[] respArr = new byte[0];

        // 车载终端数据(默认应答上传内容不做修改)
        if (1 == ORIGIN_FROM) {
            respArr = content;
            if (0x08 == cmd) {
                respArr = CommonUtil.dateToBytes(new Date());
            }
        } else {
            // 平台应答只应答时间
            if (content.length > 5) {
                byte[] dateArray = new byte[6];
                System.arraycopy(content, 0, dateArray, 0, 6);
                respArr = dateArray;
            }
        }

        // 应答内容
        byte[] respBytes = packResp(terminal, cmd, respArr);
        ctx.writeAndFlush(Unpooled.copiedBuffer(respBytes));

        // 记录下行
        KafkaUtil.send(terminal, cmd, CommonUtil.bytesToStr(respBytes), 2);
    }

    /**
     * 生成应答数据
     *
     * @param terminal
     * @param cmd
     * @param bytes
     * @return
     */
    private byte[] packResp(String terminal, int cmd, byte[] bytes) {
        int length = bytes.length;
        ByteBuf buf = Unpooled.buffer(25 + length);
        buf.writeByte(0x23);
        buf.writeByte(0x23);
        buf.writeByte(cmd);
        buf.writeByte(0x01);
        // VIN
        buf.writeBytes(terminal.getBytes());
        // 不加密
        buf.writeByte(0x01);
        buf.writeShort(length);
        // 返回数据
        buf.writeBytes(bytes);

        // 获取校验位
        byte[] content = new byte[22 + length];
        buf.getBytes(2, content);
        int check = CommonUtil.checkCode(content);
        buf.writeByte(check);

        return buf.array();
    }
}
