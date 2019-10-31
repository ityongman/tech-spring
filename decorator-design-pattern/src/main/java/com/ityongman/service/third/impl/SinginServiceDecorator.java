package com.ityongman.service.third.impl;

import com.ityongman.config.ResultMsg;
import com.ityongman.service.common.ISinginService;
import com.ityongman.service.third.ISinginServiceDecorator;

public class SinginServiceDecorator implements ISinginServiceDecorator {
    private ISinginService singinService ;

    public SinginServiceDecorator(ISinginService singinService) {
        this.singinService = singinService;
    }

    public ResultMsg register(String userName, String password) {
        return singinService.register(userName, password);
    }

    public ResultMsg registerByQQ(String openId) {
        String msg = String.format("register by QQ, openId = %s", openId);
        return new ResultMsg(200, msg, new Object());
    }

    public ResultMsg registerByWeChat(String openId) {
        String msg = String.format("register by WeChat, openId = %s", openId);
        return new ResultMsg(200, msg, new Object());
    }
}
