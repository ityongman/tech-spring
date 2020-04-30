package com.ityongman.spring;

import com.ityongman.registry.IZkRegistry;
import com.ityongman.registry.ZkRegistryCurator;
import com.ityongman.spring.annotation.RpcService;
import com.ityongman.spring.handler.ProcessorHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author shedunze
 * @Date 2020-02-02 16:17
 * @Description
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {
    private int port ;

    private Map<String, Object> handlerMap = new HashMap<>() ;

    ExecutorService pool = Executors.newCachedThreadPool();

    public RpcServer(int port) {
        this.port = port;
    }

    private IZkRegistry zkRegistry = new ZkRegistryCurator();

    /**
     * 属性处理完之后, 启动服务
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //方法1: 阻塞 的方式创建
//        try(ServerSocket serverSocket = new ServerSocket(port)){
//            while (true) {
//                Socket socket = serverSocket.accept();
//                pool.execute(new SpringProcessorHandler(socket,handlerMap));
//            }
//        }


        //方法2: NiO(Netty) 的方式创建
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast(new ObjectEncoder());
                            //NOTE: 责任链模式, 请求到达服务器后, ProcessorHandler 处理请求结果
                            pipeline.addLast(new ProcessorHandler(handlerMap));
                        }
                    });

            sb.bind(port).sync() ;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //下面代码需要注释掉, 不然会注销掉服务
//            bossGroup.shutdownGracefully() ;
//            workerGroup.shutdownGracefully() ;
        }
    }

    /**
     * bean加载完之后, 加载所有的bean信息到map中保存
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if(!serviceBeanMap.isEmpty()) {
            for (Object serviceBean : serviceBeanMap.values()) {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);

                //1. 接口类定义
                String serviceName = rpcService.value().getName();
                //2。版本定义
                String version = rpcService.version();
                if(!StringUtils.isEmpty(version)) {
                    serviceName = serviceName + "-" + version ;
                }

                // 缓存信息
                handlerMap.put(serviceName, serviceBean) ;
                // 服务注册到zookeeper
                zkRegistry.registry(serviceName, getAddr() + ":" + this.port);
            }
        }
    }


    public String getAddr() {
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return localHost.getHostAddress() ;
    }
}
