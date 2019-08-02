package com.tiza.pub.gw.netty;

/**
 * Description: GwServer
 * Author: DIYILIU
 * Update: 2019-06-27 19:31
 */

import com.tiza.pub.gw.protocol.Gb17691Decoder;
import com.tiza.pub.gw.protocol.Gb17691Handler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description: GwServer
 * Author: DIYILIU
 * Update: 2019-06-17 15:23
 */

@Slf4j
public class GwServer {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private int port;

    public void init() {
        executor.execute(() -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workGroup = new NioEventLoopGroup();

            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ch.pipeline().addLast(new Gb17691Decoder())
                                        .addLast(new Gb17691Handler());
                            }
                        });

                ChannelFuture f = b.bind(port).sync();
                log.info("GB6 网关服务器启动, 端口[{}]...", port);
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                bossGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
            }
        });
    }

    public void setPort(int port) {
        this.port = port;
    }
}

