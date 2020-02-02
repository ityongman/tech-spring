package com.ityongman.exam02.fastSeri;

import com.ityongman.ISerializer;
import com.ityongman.model.User;

/**
 * @Author shedunze
 * @Date 2020-02-01 16:09
 * @Description
 */
public class FastMain {
    public static void main(String[] args) {
        //1. 信息参数
        User user = new User();
        user.setUserName("tom");
        user.setAge(24);

        //2.1 Serilizable
        ISerializer fastSeri = new FastSerilizable();
        byte[] bytes = fastSeri.serialize(user);


        //fastJSon 不能解决 transient 修饰的字段值
        //length --> 27
        System.out.println("fast Serializable length --> " + bytes.length);

        //2.2 deSerilizable
        User deUser = fastSeri.deserialize(bytes, User.class);
        System.out.println(deUser.toString());
    }
}
