package com.ityongman.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Author shedunze
 * @Date 2020-02-02 14:11
 * @Description
 */
public class RemoteProxyHandler implements InvocationHandler {
    private String host ;
    private int port ;

    public RemoteProxyHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //1. 请求传输信息
        RpcRequest request = new RpcRequest();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setArgs(args);
        request.setVersion("2.0");


        //2. 发送逻辑
        RpcTransport transport = new RpcTransport(host,port);

        return transport.send(request);
    }
}
