package com.ityongman.exam02.xmlSeri;

import com.ityongman.ISerializer;
import com.ityongman.exam02.fastSeri.FastSerilizable;
import com.ityongman.model.User;

/**
 * @Author shedunze
 * @Date 2020-02-01 16:29
 * @Description
 */
public class XStreamMain {
    public static void main(String[] args) {
        //1. 信息参数
        User user = new User();
        user.setUserName("tom");
        user.setAge(24);

        //2.1 Serilizable
        ISerializer xStreamSeri = new XStreamSerializable();
        byte[] bytes = xStreamSeri.serialize(user);


        //length --> 241
        System.out.println("fast Serializable length --> " + bytes.length);

        //2.2 deSerilizable
        User deUser = xStreamSeri.deserialize(bytes, User.class);
        System.out.println(deUser.toString());
    }
}
