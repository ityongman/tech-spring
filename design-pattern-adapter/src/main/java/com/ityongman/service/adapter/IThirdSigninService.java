package com.ityongman.service.adapter;

import com.ityongman.common.ResultMsg;

/**
 * 支持的第三方注册方式
 */
public interface IThirdSigninService {
    ResultMsg registerByQQ(String id);

    ResultMsg registerByWechat(String id);

}
