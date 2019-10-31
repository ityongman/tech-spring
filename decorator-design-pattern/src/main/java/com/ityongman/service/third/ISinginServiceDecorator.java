package com.ityongman.service.third;

import com.ityongman.config.ResultMsg;

public interface ISinginServiceDecorator {
    ResultMsg registerByQQ(String openId);

    ResultMsg registerByWeChat(String openId);
}
