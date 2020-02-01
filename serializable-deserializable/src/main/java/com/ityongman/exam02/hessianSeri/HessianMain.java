package com.ityongman.exam02.hessianSeri;

import com.ityongman.ISerializer;
import com.ityongman.model.User;

/**
 * @Author shedunze
 * @Date 2020-02-01 16:22
 * @Description
 */
public class HessianMain {
    public static void main(String[] args) {
        //1. 信息参数
        User user = new User();
        user.setUserName("Tom");
        user.setAge(24);

        //2.1 Serilizable
        ISerializer hessianSeri = new HessianSerilizable();
        byte[] bytes = hessianSeri.serialize(user);

        //length --> 57
        System.out.println("hessian Serializable length --> " + bytes.length);


        //2.2 deSerilizable
        User deUser = hessianSeri.deserialize(bytes, User.class);
        System.out.println(deUser.toString());
    }
}
