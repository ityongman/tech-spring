package com.ityongman.server.handler;

import com.ityongman.processor.IMProcessor;
import com.ityongman.protocal.IMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author shedunze
 * @Date 2020-03-04 11:10
 * @Description 服务终端处理类
 */
@Slf4j
public class TerminalServerHandler extends SimpleChannelInboundHandler<IMessage> {
    private IMProcessor processor = new IMProcessor() ;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMessage msg) throws Exception {
        processor.sendMsg(ctx.channel(), msg) ;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Socket Client, 与客户端断连接" + cause.getMessage());

        cause.printStackTrace();
        ctx.close() ;
    }
}
