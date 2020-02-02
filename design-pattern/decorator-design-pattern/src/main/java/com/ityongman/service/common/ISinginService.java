package com.ityongman.service.common;

import com.ityongman.config.ResultMsg;

public interface ISinginService {
    ResultMsg register(String userName , String password);
}
