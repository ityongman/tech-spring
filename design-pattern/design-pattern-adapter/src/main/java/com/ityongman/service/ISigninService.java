package com.ityongman.service;

import com.ityongman.common.ResultMsg;

/**
 * 老的登陆方法
 */
public interface ISigninService {
    ResultMsg register(String userName, String pwd);
}
