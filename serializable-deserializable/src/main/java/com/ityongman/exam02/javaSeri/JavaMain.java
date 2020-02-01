package com.ityongman.exam02.javaSeri;

import com.ityongman.ISerializer;
import com.ityongman.model.User;

/**
 * @Author shedunze
 * @Date 2020-02-01 15:32
 * @Description
 */
public class JavaMain {
    public static void main(String[] args) {
        //1. 信息参数
        User user = new User();
        user.setUserName("Tom");
        user.setAge(24);

        //2.1 Serilizable
        ISerializer javaSeri = new JavaSerilizable();
        byte[] bytes = javaSeri.serialize(user);

        //length --> 157
        System.out.println("Java Serializable length --> " + bytes.length);


        //2.2 deSerilizable
        User deUser = javaSeri.deserialize(bytes, null);
        System.out.println(deUser.toString());
    }
}
