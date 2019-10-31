package com.ityongman.service.common.impl;

import com.ityongman.config.ResultMsg;
import com.ityongman.service.common.ISinginService;

public class SinginService implements ISinginService {
    public ResultMsg register(String userName, String password) {
        String msg = String.format("register by common type, userName=%s , password=%s", userName, password);
        return new ResultMsg(200, msg, new Object());
    }
}
