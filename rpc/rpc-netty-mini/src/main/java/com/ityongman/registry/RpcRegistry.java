package com.ityongman.registry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @Author shedunze
 * @Date 2020-03-02 15:48
 * @Description 注册中心, 用于服务注册 和 服务发现的功能
 */
public class RpcRegistry {

    /**
     * 注册中心是一个 服务,
     * port 服务占用端口
     */
    private int port;

    public RpcRegistry(int port) {
        this.port = port ;
    }

    /**
     * RpcRegistry 是Server端, 提供服务注册和服务发现的功能
     */
    public void start() {
        /**
         * boss 工作线程组
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        /**
         * worker 工作线程组
         */
        EventLoopGroup workerGroup = new NioEventLoopGroup() ;

        /**
         * 创建server
         */
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 添加通道
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 添加处理器
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            ChannelPipeline pipeline = sc.pipeline();
                            /**
                             * 责任链的方式添加相关处理逻辑信息
                             */
                            /**
                             * LengthFieldBasedFrameDecoder(
                             *             int maxFrameLength,  // 框架的最大长度。如果帧的长度大于此值，则将抛出 TooLongFrameException
                             *             int lengthFieldOffset, // 长度字段的偏移量：即对应的长度字段在整个消息数据中得位置
                             *             int lengthFieldLength, // 长度字段的长度。如：长度字段是 int 型表示，那么这个值就是 4（long 型就是 8）
                             *             int lengthAdjustment, // 要添加到长度字段值的补偿值
                             *             int initialBytesToStrip // 从解码帧中去除的第一个字节数
                             * )
                             */
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            /**
                             * LengthFieldPrepender(int lengthFieldLength) // prepended 字段的长度, 只允许1, 2, 3, 4, and 8
                             */
                            pipeline.addLast(new LengthFieldPrepender(4));
                            /**
                             * 编码 encode
                             */
                            pipeline.addLast("encoder", new ObjectEncoder());
                            /**
                             * 解码 decode
                             */
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            /**
                             * 注册中心注册处理器
                             */
                            pipeline.addLast(new RegistryHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); //保持长链接

            ChannelFuture future = sb.bind(port).sync();
            System.out.println("服务监听在 --> " + port);

            future.channel().closeFuture().sync() ;
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new RpcRegistry(8081).start();
    }
}
