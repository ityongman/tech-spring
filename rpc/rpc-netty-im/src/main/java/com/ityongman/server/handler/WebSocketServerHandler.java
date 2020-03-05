package com.ityongman.server.handler;

import com.ityongman.processor.IMProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author shedunze
 * @Date 2020-03-04 11:27
 * @Description webSocket 请求处理
 */
@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private IMProcessor processor = new IMProcessor() ;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        processor.sendMsg(ctx.channel(), msg.text());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String addr = processor.getAddr(ctx.channel());
        log.error("WebSocket client " + addr + "异常");

        cause.printStackTrace();
        ctx.close() ;
    }
}
