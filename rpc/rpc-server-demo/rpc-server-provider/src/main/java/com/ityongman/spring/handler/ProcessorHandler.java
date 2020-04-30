package com.ityongman.spring.handler;

import com.ityongman.service.RpcRequest;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author shedunze
 * @Date 2020-03-10 14:57
 * @Description
 */
public class ProcessorHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private Map<String , Object> handlerMap = new HashMap<>();

    public ProcessorHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        //反射处理, 获取调用结果
        Object result = process(msg);
        //响应结果
        ctx.writeAndFlush(result).addListener(ChannelFutureListener.CLOSE) ;
    }

    private Object process(RpcRequest request) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MethodInvoke invoke = new MethodInvoke(handlerMap);

        return invoke.invoke(request);
    }
}
