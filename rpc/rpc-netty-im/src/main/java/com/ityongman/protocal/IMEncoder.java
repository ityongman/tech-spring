package com.ityongman.protocal;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * @Author shedunze
 * @Date 2020-03-04 10:47
 * @Description 对通信信息进行编码
 */
public class IMEncoder extends MessageToByteEncoder<IMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, IMessage msg, ByteBuf out) throws Exception {
        out.writeBytes(new MessagePack().write(msg)) ;
    }

    public String encode(IMessage msg) {
        if (null == msg) {
            return "" ;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[" + msg.getOperate() + "]").append("[" + msg.getTime() + "]") ;

        if (IMProtocal.LOGIN.getType().equalsIgnoreCase(msg.getOperate())
            || IMProtocal.FLOWER.getType().equalsIgnoreCase(msg.getOperate())) {
            sb.append("[" + msg.getSender() + "]").append("[" + msg.getTerminal() + "]") ;
        } else if(IMProtocal.CHAT.getType().equalsIgnoreCase(msg.getOperate())) {
            sb.append("[" + msg.getSender() + "]") ;
        } else if (IMProtocal.SYSTEM.getType().equalsIgnoreCase(msg.getOperate())) {
            sb.append("[" + msg.getOnline() + "]") ;
        }

        if (msg.getContent() != null && msg.getContent().trim().length() > 0) {
            sb.append(" - " + msg.getContent());
        }

        return sb.toString() ;
    }
}
