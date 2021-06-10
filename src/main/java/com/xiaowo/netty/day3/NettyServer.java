package com.xiaowo.netty.day3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.Future;

/**
 * Created by wubo15 on 2021/6/8.
 *
 * @author wubo15
 * @date 2021/6/8
 */
public class NettyServer {

    public static void main(String[] args) {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new FirstServerHandler());
                    }
                });

        serverBootstrap.childAttr(AttributeKey.newInstance("clientKey"), "clientValue");

        serverBootstrap
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);

        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);


        bind(serverBootstrap, 1000);


    }


    private static void bind(final ServerBootstrap serverBootstrap, final int port) {

        GenericFutureListener<? extends io.netty.util.concurrent.Future<? super Void>> genericFutureListener = new GenericFutureListener<io.netty.util.concurrent.Future<? super Void>>() {
            public void operationComplete(io.netty.util.concurrent.Future<? super Void> future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("端口[" + port + "]绑定成功!");
                } else {
                    System.err.println("端口[" + port + "]绑定失败!");
                    bind(serverBootstrap, port + 1);
                }
            }
        };

        serverBootstrap.bind(port).addListener(genericFutureListener);
    }

}
