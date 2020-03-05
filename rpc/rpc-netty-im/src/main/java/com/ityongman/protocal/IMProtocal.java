package com.ityongman.protocal;

/**
 * @Author shedunze
 * @Date 2020-03-04 09:28
 * @Description 自定义通讯(IM)时使用的通信协议
 */
public enum IMProtocal {
    SYSTEM("SYSTEM"),
    LOGIN("LOGIN"),
    LOGOUT("LOGOUT"),
    CHAT("CHAT"),
    FLOWER("FLOWER");

    private String type ;

    IMProtocal(String type) {
        this.type = type;
    }

    public static boolean isIMProtocal(String msg) {
        return msg.matches("^\\[(SYSTEM|LOGIN|LOGOUT|CHAT)\\]");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
