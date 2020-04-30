package com.ityongman.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @Author shedunze
 * @Date 2020-02-02 14:24
 * @Description
 */
public class RpcTransport extends SimpleChannelInboundHandler<Object> {
    //1. 老方式 远程调用
    private String host ;
    private int port ;

    public RpcTransport(String host, int port) {
        this.host = host ;
        this.port = port ;
    }

    //2. 新方式 远程调用
    private String serviceAddr ;
    private Object result ;

    public RpcTransport(String serviceAddr) {
        this.serviceAddr = serviceAddr;
    }

//    public Object send(RpcRequest request) {
//        Object result = null ;
//        ObjectOutputStream outPut = null ;
//        ObjectInputStream input = null ;
//
//        try (Socket socket = new Socket(host, port)) {
//            //1. 发送数据
//            outPut = new ObjectOutputStream(socket.getOutputStream()) ;
//            outPut.writeObject(request);
//            outPut.flush();
//            //2. 接收数据
//            input = new ObjectInputStream(socket.getInputStream()) ;
//            result = input.readObject();
//
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        return result ;
//    }

    public Object send(RpcRequest request) {
        EventLoopGroup worker = new NioEventLoopGroup() ;

        try {
            Bootstrap b = new Bootstrap() ;
            b.group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                            .addLast(new ObjectEncoder())
                            .addLast(RpcTransport.this);

                    }
                })
                .option(ChannelOption.TCP_NODELAY, true);

            String[] url = serviceAddr.split(":");
            ChannelFuture future = b.connect(url[0], Integer.parseInt(url[1])).sync();

            future.channel().writeAndFlush(request).sync();

            if(request!=null){
                future.channel().closeFuture().sync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully() ;
        }

        return result ;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.result = msg ;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("发生异常");
        cause.printStackTrace();

        ctx.close() ;
    }
}
