package com.ityongman.server;

import com.ityongman.protocal.IMDecoder;
import com.ityongman.protocal.IMEncoder;
import com.ityongman.server.handler.HttpServerHandler;
import com.ityongman.server.handler.TerminalServerHandler;
import com.ityongman.server.handler.WebSocketServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;


/**
 * @Author shedunze
 * @Date 2020-03-04 09:08
 * @Description 通信 服务端启动类入口
 */
@Slf4j
public class IMServer {
    private int port = 18080 ;

    public void start() {
        start(this.port);
    }

    public void start(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap sbs = new ServerBootstrap() ;

            sbs.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            //1. 解析自定义协议
                            pipeline.addLast(new IMDecoder()) ; // 解码
                            pipeline.addLast(new IMEncoder()) ; // 编码
                            pipeline.addLast(new TerminalServerHandler());

                            //2. 解析http请求
                            pipeline.addLast(new HttpServerCodec());
                            //主要是将同一个http请求或响应的多个消息对象变成一个 fullHttpRequest完整的消息对象
                            pipeline.addLast(new HttpObjectAggregator(1024 * 64));
                            //处理大数据流,比如一个1G大小的文件如果你直接传输肯定会撑暴jvm内存的 ,加上这个handler我们就不用考虑这个问题了
                            pipeline.addLast(new ChunkedWriteHandler());
                            pipeline.addLast(new HttpServerHandler()) ;

                            //3. 解析webSocket请求
                            pipeline.addLast(new WebSocketServerProtocolHandler("/im"));
                            pipeline.addLast(new WebSocketServerHandler());
                        }
                    });
            ChannelFuture future = sbs.bind(port).sync();
            log.info("服务已启动,监听端口" + this.port);

            future.channel().closeFuture().sync() ;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully() ;
            workerGroup.shutdownGracefully() ;
        }
    }

    public static void main(String[] args) {
        new IMServer().start();
    }
}
