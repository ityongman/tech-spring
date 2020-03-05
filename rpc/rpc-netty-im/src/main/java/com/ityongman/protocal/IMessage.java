package com.ityongman.protocal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.msgpack.annotation.Message;

/**
 * @Author shedunze
 * @Date 2020-03-04 10:07
 * @Description client 和 server 之间进行消息通信的通信实体
 */
//MessagePack对信息编码需要的注解
@Message
@Getter
@Setter
@ToString
public class IMessage {
    //通信地址(IP + PORT)
    private String address ;
    //操作, SYSTEM、LOGIN、LOGOUT、CHAT、FLOWER
    private String operate ;
    //发送命令时间
    private long time ;
    //发送者
    private String sender ;
    //接受者
    private String reciver ;
    //终端
    private String terminal ;
    //消息内容
    private String content ;
    //在线人数
    private int online ;


    public IMessage() {
    }

    /**
     * system
     */
    public IMessage(String operate, long time , int online, String content) {
        this.operate = operate ;
        this.time = time ;
        this.online = online ;
        this.content = content ;
    }

    /**
     * logout
     */
    public IMessage(String operate, long time, String sender) {
        this.operate = operate;
        this.time = time;
        this.sender = sender;
    }

    /**
     * login 、flower
     */
    public IMessage(String operate, long time, String sender, String terminal) {
        this.operate = operate;
        this.time = time;
        this.sender = sender;
        this.terminal = terminal;
    }

    /**
     * chat
     */
    public IMessage(String operate, String sender, long time, String content) {
        this.operate = operate;
        this.sender = sender;
        this.time = time;
        this.content = content;
    }
}
