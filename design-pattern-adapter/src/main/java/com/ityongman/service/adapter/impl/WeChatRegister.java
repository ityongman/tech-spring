package com.ityongman.service.adapter.impl;

import com.ityongman.common.ResultMsg;
import com.ityongman.service.adapter.IRegisterAdapter;

public class WeChatRegister implements IRegisterAdapter {
    public boolean support(Object obj) {
        return obj instanceof WeChatRegister;
    }

    public ResultMsg register(String id) {
        return new ResultMsg(200, "register by Wechat success", new Object());
    }
}
