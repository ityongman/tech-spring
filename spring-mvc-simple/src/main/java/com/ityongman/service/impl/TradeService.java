package com.ityongman.service.impl;

import com.ityongman.annotation.MyService;
import com.ityongman.service.ITradeService;

@MyService
public class TradeService implements ITradeService {
    // TradeService 是单例的, 这个变量是用来说明 bean安全与否与Spring没有关系
    private int i = 1 ;

    @Override
    public String query(String tid) {
        return "TradeService request param is " + tid + ", i = " + i++;
    }
}
