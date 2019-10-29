package com.ityongman.service.adapter.impl;

import com.ityongman.common.ResultMsg;
import com.ityongman.service.adapter.IRegisterAdapter;

public class QQRegister implements IRegisterAdapter {
    public boolean support(Object obj) {
        return obj instanceof QQRegister;
    }

    public ResultMsg register(String id) {
        return new ResultMsg(200, "register by QQ success", new Object());
    }
}
