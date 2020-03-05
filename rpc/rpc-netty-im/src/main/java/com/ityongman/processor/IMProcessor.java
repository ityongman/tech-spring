package com.ityongman.processor;

import com.alibaba.fastjson.JSONObject;
import com.ityongman.protocal.IMDecoder;
import com.ityongman.protocal.IMEncoder;
import com.ityongman.protocal.IMProtocal;
import com.ityongman.protocal.IMessage;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @Author shedunze
 * @Date 2020-03-04 11:15
 * @Description
 */
public class IMProcessor {
    //缓存, 记录在线用户
    private static ChannelGroup onlineUsers = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private IMDecoder decoder = new IMDecoder();
    private IMEncoder encoder = new IMEncoder();

    private AttributeKey<String> NICK_NAME = AttributeKey.valueOf("nickName") ;
    private AttributeKey<String> IP_ADDR = AttributeKey.valueOf("ipAddr");
    private AttributeKey<String> FROM = AttributeKey.valueOf("from") ;
    private AttributeKey<JSONObject> ATTRS = AttributeKey.valueOf("attrs") ;

    /**
     * 发送消息
     * @param channel 客户端
     * @param msg 消息体
     */
    public void sendMsg(Channel channel, IMessage msg) {
        sendMsg(channel, encoder.encode(msg));
    }

    /**
     * 发送消息
     * @param channel
     * @param msg
     */
    public void sendMsg(Channel channel , String msg){
        IMessage req = decoder.decode(msg);

        //消息解析
        String addr = getAddr(channel);
        if (IMProtocal.LOGIN.getType().equalsIgnoreCase(req.getOperate())) {
            //sender
            channel.attr(NICK_NAME).getAndSet(req.getSender()) ;
            //addr
            channel.attr(IP_ADDR).getAndSet(addr);
            //terminal
            channel.attr(FROM).getAndSet(req.getTerminal()) ;

            onlineUsers.add(channel);

            for (Channel onlineUser : onlineUsers){
                boolean isSelf = onlineUser == channel ;
                // 同一用户
                if (isSelf) {
                    req = new IMessage(IMProtocal.SYSTEM.getType(), System.currentTimeMillis(), onlineUsers.size(), "已与服务建立连接");
                } else {
                    req = new IMessage(IMProtocal.SYSTEM.getType(), System.currentTimeMillis(), onlineUsers.size(), getNickName(channel) + "加入");
                }

                if("Console".equalsIgnoreCase(channel.attr(FROM).get())) {
                    channel.writeAndFlush(req) ;
                    continue ;
                }

                String content = encoder.encode(req);
                /**注意 channel 的选择, 不然信息传输会存在问题*/
                onlineUser.writeAndFlush(new TextWebSocketFrame(content)) ;
            }
        } else if(IMProtocal.CHAT.getType().equalsIgnoreCase(req.getOperate())) {
            for (Channel onlineUser : onlineUsers) {
                boolean isSelf = onlineUser == channel ;
                if (isSelf) {
                    req.setSender("You");
                } else {
                    req.setSender(getNickName(channel));
                }

                req.setTime(System.currentTimeMillis());
                if ("Console".equalsIgnoreCase(channel.attr(FROM).get())
                        && !isSelf) {
                    channel.writeAndFlush(req) ;

                    continue ;
                }
                String content = encoder.encode(req) ;
                /**
                 * 注意 channel 的选择, 不然信息传输会存在问题
                 */
                onlineUser.writeAndFlush(new TextWebSocketFrame(content)) ;
            }
        } else if(IMProtocal.FLOWER.getType().equalsIgnoreCase(req.getOperate())) {
            JSONObject attrs = getAttrs(channel) ;

            long currTime = System.currentTimeMillis() ;

            if (null != attrs) {
                // 获取上次鲜花时间
                long lastFlowerTime = attrs.getLongValue("lastFlowerTime");
                // 10s 不允许重复发送鲜花
                int timeGap = 10 ;
                long sub = currTime - lastFlowerTime ;

                //1. 发送太多频繁
                if (sub < 1000 * timeGap) {
                    req.setSender("You");
                    req.setTime(currTime);
                    req.setOperate(IMProtocal.SYSTEM.getType());
                    req.setContent("您发送鲜花的时间间隔太短, 请 " + (timeGap - Math.round(sub / 1000)) + "秒之后再试");

                    String content = encoder.encode(req) ;
                    channel.writeAndFlush(new TextWebSocketFrame(content)) ;

                    return ;
                }

                //2. 正常发送速率
                for (Channel onlineUser : onlineUsers) {
                    boolean isSelf = onlineUser == channel ;

                    //自己
                    if (isSelf) {
                        req.setContent("您给大家发送了鲜花");
                        req.setOperate(IMProtocal.SYSTEM.getType());
                        req.setTime(currTime);
                        req.setSender("You");

                        setAttrs(channel, "lastFlowerTime", currTime) ;
                    } else {
                        req.setContent(getNickName(channel) + " 送来一波鲜花雨");
                        req.setSender(getNickName(channel));
                    }

                    String contect = encoder.encode(req);
                    /**
                     * 注意 channel 的选择, 不然信息传输会存在问题
                     */
                    onlineUser.writeAndFlush(new TextWebSocketFrame(contect)) ;
                }
            }
        }
    }


    /**
     * 设置属性
     */
    private void setAttrs(Channel channel, String lastFlowerTime , long currTime) {
        JSONObject attrs = channel.attr(ATTRS).get();
        attrs.put(lastFlowerTime, currTime) ;
        channel.attr(ATTRS).set(attrs);
    }
    /**
     * 获取属性
     */
    private JSONObject getAttrs(Channel channel) {
        return channel.attr(ATTRS).get() ;
    }


    /**
     * 获取用户名
     * @param channel
     * @return
     */
    private String getNickName(Channel channel) {
        return channel.attr(NICK_NAME).get() ;
    }

    /**
     * 获取地址
     * @param channel
     * @return
     */
    public String getAddr(Channel channel) {
        return channel.remoteAddress().toString().replaceFirst("/","");
    }
}
