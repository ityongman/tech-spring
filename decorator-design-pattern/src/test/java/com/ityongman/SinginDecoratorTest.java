package com.ityongman;

import com.ityongman.config.ResultMsg;
import com.ityongman.service.common.ISinginService;
import com.ityongman.service.common.impl.SinginService;
import com.ityongman.service.third.impl.SinginServiceDecorator;

public class SinginDecoratorTest {
    public static void main(String[] args) {
        ISinginService singinService = new SinginService();
        SinginServiceDecorator singinDecorator = new SinginServiceDecorator(singinService);

        ResultMsg result1 = singinDecorator.register("Bob", "12345");
        ResultMsg result2 = singinDecorator.registerByQQ("aabbcc");
        ResultMsg result3 = singinDecorator.registerByWeChat("hahahoho");

        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
    }
}
