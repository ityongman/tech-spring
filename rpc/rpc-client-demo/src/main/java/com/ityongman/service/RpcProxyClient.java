package com.ityongman.service;

import java.lang.reflect.Proxy;

/**
 * @Author shedunze
 * @Date 2020-02-02 14:07
 * @Description 代理方式获取客户端代理对象
 */
public class RpcProxyClient {
    public <T> T clientProxy(final Class interfaceClass , final String host, int port) {
        /**
         * classLoader
         * interfacesClass []
         * InvocationHandler
         */
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new RemoteProxyHandler(host,port));
    }
}
