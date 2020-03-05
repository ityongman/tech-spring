package com.ityongman.client;

import com.ityongman.client.handler.IMClientHandler;
import com.ityongman.protocal.IMEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Author shedunze
 * @Date 2020-03-04 09:09
 * @Description
 */
public class IMClient {
    private IMClientHandler imClientHandler ;

    public IMClient(String nickName) {
        imClientHandler = new IMClientHandler(nickName);
    }

    private void connect(String host, int port) {
        EventLoopGroup workerGroup = new NioEventLoopGroup() ;

        try{
            Bootstrap bs = new Bootstrap();
            bs.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new IMEncoder());
                        pipeline.addLast(new IMEncoder());
                        pipeline.addLast(imClientHandler) ;
                    }
                });

            ChannelFuture future = bs.connect(host, port).sync();
            future.channel().closeFuture().sync() ;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully() ;
        }
    }

    public static void main(String[] args) {
        new IMClient("dabaicai").connect("127.0.0.1", 18080);

        String url = "http://localhost:8080/images/a.png";
        System.out.println(url.toLowerCase().matches(".*\\.(gif|png|jpg)$"));
    }
}
