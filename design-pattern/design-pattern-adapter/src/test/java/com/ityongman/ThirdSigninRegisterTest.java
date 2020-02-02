package com.ityongman;

import com.ityongman.common.ResultMsg;
import com.ityongman.service.adapter.impl.ThirdSigninService;

public class ThirdSigninRegisterTest {
    public static void main(String[] args) {
        ThirdSigninService signinService = new ThirdSigninService();
        //1.
        ResultMsg register = signinService.register("tom", "123456");
        System.out.println(register.toString());
        //2.
        ResultMsg qq = signinService.registerByQQ("10010");
        System.out.println(qq.toString());

        //3.
        ResultMsg wechat = signinService.registerByWechat("wechat");
        System.out.println(wechat.toString());
    }
}
