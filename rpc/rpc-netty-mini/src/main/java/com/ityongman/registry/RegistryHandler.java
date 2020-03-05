package com.ityongman.registry;


import com.ityongman.protocal.InvokerProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author shedunze
 * @Date 2020-03-02 16:15
 * @Description
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {
    /**
     * 缓存, 存储所有注册的 服务提供者
     */
    private Map<String/**serviceName*/, Object /**serviceObj*/> registryMap = new ConcurrentHashMap<>() ;

    /**
     * 保存所有服务提供类名
     */
    private List<String> classNames = new ArrayList<>() ;

    public RegistryHandler() {
        /**
         * 1。扫描所有的类
         */
        scanClass("com.ityongman.provider.impl") ;

        /**
         * 2。服务注册
         */
        doRegistry();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        InvokerProtocol req = (InvokerProtocol) msg;
        /**
         * 1. 客户端发送请求时, 查询缓存中是否有 当前服务
         * 2. 通过反射调用的方式, 进行服务调用
         */

        Object result = new Object();
        if(registryMap.containsKey(req.getClassName())) {
            Object clazz = registryMap.get(req.getClassName());
            Method method = clazz.getClass().getMethod(req.getMethod(), req.getParames());
            result = method.invoke(clazz, req.getValues()) ;
        }

        ctx.write(result);
        ctx.flush() ;
        ctx.close() ;
    }

    /**
     * 输出异常信息, 关闭ctx
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close() ;
    }

    /**
     * 服务注册
     */
    private void doRegistry() {
        //1. 集合校验
        if (classNames == null || classNames.size() == 0) {
            return ;
        }
        classNames.stream().forEach(className -> {
            try {
                Class<?> clazz = Class.forName(className);
                Class<?> anInterface = clazz.getInterfaces()[0];

                //clazz.getInterfaces() -> 数组
                registryMap.put(anInterface.getName(), clazz.newInstance());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        });
    }

    private void scanClass(String packageName) {
        //1. 将所有的 .  替换为 /
        //获取路径信息
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        //加载文件
        File parentFile = new File(url.getFile());
        for (File childFile : parentFile.listFiles()) {
            if (childFile.isDirectory()) {
                scanClass(packageName + "." + childFile.getName());
            } else {
                classNames.add(packageName + "." + childFile.getName().replace(".class", "").trim()) ;
            }
        }
    }
}
