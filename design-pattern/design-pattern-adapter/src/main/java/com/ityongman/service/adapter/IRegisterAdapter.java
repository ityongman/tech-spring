package com.ityongman.service.adapter;

import com.ityongman.common.ResultMsg;

/**
 * 适配器类, 适配器不一定要有接口
 */
public interface IRegisterAdapter {
    /**
     * 当前obj是否支持该适配器
     * @param obj
     * @return
     */
    boolean support(Object obj);

    ResultMsg register(String id);
}
