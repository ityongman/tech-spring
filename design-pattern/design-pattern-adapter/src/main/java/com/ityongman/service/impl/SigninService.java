package com.ityongman.service.impl;

import com.ityongman.common.ResultMsg;
import com.ityongman.service.ISigninService;

public class SigninService implements ISigninService {
    public ResultMsg register(String userName, String pwd) {
        return new ResultMsg(200, "Register success", new Object());
    }
}
