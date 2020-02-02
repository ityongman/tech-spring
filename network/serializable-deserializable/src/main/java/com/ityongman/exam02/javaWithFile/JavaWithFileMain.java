package com.ityongman.exam02.javaWithFile;

import com.ityongman.ISerializer;
import com.ityongman.model.User;

/**
 * @Author shedunze
 * @Date 2020-02-01 16:47
 * @Description
 */
public class JavaWithFileMain {
    public static void main(String[] args) {
        //1. 信息参数
        User user = new User();
        user.setUserName("Tom");
        user.setAge(24);

        //2.1 Serilizable
        ISerializer javaWithFileSeri = new JavaWithFileSerilizable();
        byte[] bytes = javaWithFileSeri.serialize(user);

        //length --> 157
        System.out.println("Java Serializable length --> " + bytes.length);

    }
}
