package com.ityongman.service;

import com.ityongman.discovery.IZkDiscovery;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Author shedunze
 * @Date 2020-02-02 14:11
 * @Description
 */
public class RemoteProxyHandler implements InvocationHandler {
    //1.无注册中心方式
    private String host ;
    private int port ;

    public RemoteProxyHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }

    //2. 有注册中心方式
    private IZkDiscovery zkDiscovery ;

    private String version ;

    public RemoteProxyHandler(IZkDiscovery zkDiscovery, String version) {
        this.zkDiscovery = zkDiscovery;
        this.version = version;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        /* 老方式 远程调用
        //1. 请求传输信息
        RpcRequest request = new RpcRequest();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setArgs(args);
        request.setVersion(this.version);

        //2. 发送逻辑 -> old
        RpcTransport transport = new RpcTransport(host,port);
        return transport.send(request);*/

        //新方式远程调用
        //1. 请求传输信息
        RpcRequest request = new RpcRequest();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParamTypes(method.getParameterTypes());
        request.setArgs(args);
        request.setVersion(this.version);

        //2. new -> 服务接口路径
        String servicePath = request.getClassName() ;
        if (!StringUtils.isEmpty(version)) {
            servicePath += "-" + version ;
        }
        //3. 去注册中心发现服务
        String serviceAddr = zkDiscovery.discovery(servicePath);

        RpcTransport transport = new RpcTransport(serviceAddr) ;
        return transport.send(request);
    }
}
