package com.ityongman.client.handler;

import com.ityongman.protocal.IMProtocal;
import com.ityongman.protocal.IMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author shedunze
 * @Date 2020-03-04 11:40
 * @Description 客户端消息处理类
 */
@Slf4j
public class IMClientHandler extends SimpleChannelInboundHandler<IMessage> {
    private ChannelHandlerContext ctx ;

    private String nickName ;

    public IMClientHandler(String nickName) {
        this.nickName = nickName ;
    }


    /**
     * tcp建立连接后调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx ;
        /**
         * login
         */
        IMessage msg = new IMessage(IMProtocal.LOGIN.getType(), System.currentTimeMillis(), nickName, "Console");
        sendMsg(msg) ;

        log.info("成功连接服务器, 服务登陆");
        session();
    }

    /**
     * 收到消息后调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMessage msg) throws Exception {
        String sender = msg.getSender() == null ? "" : msg.getSender() + ":" ;
        System.out.println(sender + removeHtmlTag(msg.getContent()));
    }

    /**
     * 发生异常调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("与服务器断开连接: " + cause.getMessage());

        cause.printStackTrace();
    }

    private void session() {
        Thread t1 = new Thread(() -> {
            System.out.println(nickName + " 您好, 请输入聊天内容");
            Scanner in = new Scanner(System.in) ;
            IMessage msg = null;
            do {
                if(in.hasNext()) {
                    String input = in.next() ;
                    if ("exit".equalsIgnoreCase(input)) {
                        msg = new IMessage(IMProtocal.LOGOUT.getType(), System.currentTimeMillis(), nickName);
                    } else {
                        msg = new IMessage(IMProtocal.CHAT.getType(), nickName, System.currentTimeMillis(), input);
                    }
                }

            }while(sendMsg(msg));

            in.close();
        }) ;

        t1.start();
    }

    /**
     * 发送消息
     * @param msg
     */
    private boolean sendMsg(IMessage msg) {
        this.ctx.channel().writeAndFlush(msg) ;
        System.out.println("请开始你的聊天:");
        return msg.getOperate().equalsIgnoreCase(IMProtocal.LOGOUT.getType()) ? false : true ;
    }

    private String removeHtmlTag(String htmlStr) {
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
        Matcher m_script=p_script.matcher(htmlStr);
        htmlStr=m_script.replaceAll(""); //过滤script标签

        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
        Matcher m_style=p_style.matcher(htmlStr);
        htmlStr=m_style.replaceAll(""); //过滤style标签

        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
        Matcher m_html=p_html.matcher(htmlStr);
        htmlStr=m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }
}
