package com.ityongman.protocal;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author shedunze
 * @Date 2020-03-04 09:33
 * @Description IM 协议消息解码类
 */
public class IMDecoder extends ByteToMessageDecoder {
    //解析IM写一下请求内容的正则
    private Pattern pattern = Pattern.compile("^\\[(.*)\\](\\s\\-\\s(.*))?");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            //1. 获取可读字节长度
            final int length = in.readableBytes();
            //2. 获取传输的消息
            final byte[] arr = new byte[length];
            String msg = new String(arr, in.readerIndex(), length);

            //3. 消息是否满足要求
            if (msg != null && msg.trim().length() > 0) {
                // 不符合协议消息体, 略过不处理
                if (!IMProtocal.isIMProtocal(msg)) {
                    ctx.channel().pipeline().remove(this);

                    return;
                }
            }

            in.getBytes(in.readerIndex(), arr, 0, length);
            out.add(new MessagePack().read(arr, IMessage.class));
            in.clear();
        } catch (Exception e) {
            ctx.channel().pipeline().remove(this);
        }
    }

    //将msg解析成 IMessage
    public IMessage decode(String msg) {
        //1. msg 不能为空
        if (null == msg || msg.trim().length() <= 0) {
            return null ;
        }
        //2. 正则解析msg
        Matcher matcher = pattern.matcher(msg);
        String header = "";
        String content = "";
        if (matcher.matches()) {
            header = matcher.group(1);
            content = matcher.group(3);
        }
        String[] headers = header.split("\\]\\[");


        long time = Long.parseLong(headers[1]);

        if (msg.startsWith("[" + IMProtocal.LOGIN.getType() + "]")){
            /**
             * [命令][命令发送时间][命令发送人][终端类型]
             * [LOGIN][124343423123][dabaicai][WebSocket]
             */
            return new IMessage(headers[0], time, headers[2], headers[3]) ;
        } else if (msg.startsWith("[" + IMProtocal.LOGOUT.getType() + "]")){
            /**
             * [命令][命令发送时间][命令发送人]
             * [LOGOUT][124343423123][dabaicai]
             */
            return new IMessage(headers[0], time, headers[2]);
        } else if (msg.startsWith("["+  IMProtocal.CHAT.getType() +"]")) {
            /**
             * [命令][命令发送时间][命令发送人][命令接收人] – 聊天内容例如
             * [CHAT][124343423123][dabaicai][ALL] – 这是一个聊天内容
             */
            return new IMessage(headers[0],headers[2], time, content);
        } else if (msg.startsWith("["+  IMProtocal.FLOWER.getType() +"]")){
            /**
             * [命令][命令发送时间][命令发送人][终端类型][命令接收人]
             * [FLOWER][124343423123][dabaicai][WebSocket][ALL]
             */
            return new IMessage(headers[0], time, headers[2], headers[3]) ;
        } else {
            return null ;
        }
    }
}
