package com.ityongman.consumer;

import com.ityongman.protocal.InvokerProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author shedunze
 * @Date 2020-03-02 17:03
 * @Description 创建请求端的代理对象
 */
public class RpcProxy {

    public static <T> T create(Class<?> clazz) {
        /**
         * 1. 获取接口对象
         */
        Class[] classes = clazz.isInterface() ? new Class[]{clazz} : clazz.getInterfaces();

        /**
         * 创建方法InvocationHandler
         */
        MethodProxy methodHandler = new MethodProxy(clazz);

        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),classes, methodHandler);
    }

    private static class MethodProxy implements InvocationHandler {
        private Class<?> clazz;

        public MethodProxy(Class<?> clazz) {
            this.clazz = clazz ;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //1. 代理的是具体的类
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            //2. 传递过来的是核心接口类
            } else {
                return rpcInvoke(proxy, method, args);
            }
        }

        private Object rpcInvoke(Object proxy, Method method, Object[] args) {
            InvokerProtocol invokerProtocol = new InvokerProtocol();
            // this.getClass().getName() 代理对象
            invokerProtocol.setClassName(this.clazz.getName()); // 类名
            invokerProtocol.setMethod(method.getName()); // 方法名
            invokerProtocol.setParames(method.getParameterTypes()); // 方法参数类型
            invokerProtocol.setValues(args); // 方法参数对应的值

            EventLoopGroup workerGroup = new NioEventLoopGroup();
            ConsumerProxyHandler consumerProxyHandler = new ConsumerProxyHandler() ;
            try {
                Bootstrap bs = new Bootstrap();
                bs.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));

                                pipeline.addLast("encoder", new ObjectEncoder());
                                pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                                pipeline.addLast("handler", consumerProxyHandler);
                            }
                        })
                        .option(ChannelOption.TCP_NODELAY, true);

                ChannelFuture future = bs.connect("127.0.0.1", 8081).sync();
                future.channel().writeAndFlush(invokerProtocol).sync() ;
                future.channel().closeFuture().sync() ;
            } catch (Exception e) {
                workerGroup.shutdownGracefully();

                e.printStackTrace(); ;
            }

            return consumerProxyHandler.getResponse() ;
        }
    }
}
