package com.ityongman.exam02.javaWithFile;

import com.ityongman.ISerializer;
import com.ityongman.model.User;

/**
 * @Author shedunze
 * @Date 2020-02-01 16:51
 * @Description 序列化、反序列化分别注释、开启 serialVersionUID = 1L 会有下面的错误抛出
 * class incompatible: stream classdesc serialVersionUID = 8459627334823184093,
 * local class serialVersionUID = 1
 */
public class JavaWithFileMain2 {
    public static void main(String[] args) {
        //2.2 deSerilizable
        ISerializer javaWithFileSeri = new JavaWithFileSerilizable();
        User deUser = javaWithFileSeri.deserialize(null, User.class);
        System.out.println(deUser.toString());
    }
}
