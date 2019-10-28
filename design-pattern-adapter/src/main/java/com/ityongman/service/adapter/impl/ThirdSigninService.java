package com.ityongman.service.adapter.impl;

import com.ityongman.common.ResultMsg;
import com.ityongman.service.adapter.IRegisterAdapter;
import com.ityongman.service.adapter.IThirdSigninService;
import com.ityongman.service.impl.SigninService;

public class ThirdSigninService extends SigninService implements IThirdSigninService {
    public ResultMsg registerByQQ(String id) {
        return processRegister(id, QQRegister.class);
    }

    public ResultMsg registerByWechat(String id) {
        return processRegister(id, WeChatRegister.class);
    }

    private ResultMsg processRegister(String id, Class<? extends IRegisterAdapter> clazz) {
        try {
            IRegisterAdapter registerAdapter = clazz.newInstance();

            if(registerAdapter.support(registerAdapter)) {
                return registerAdapter.register(id);
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
