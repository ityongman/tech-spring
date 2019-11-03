package com.ityongman.service.impl;

import com.ityongman.annotation.MyService;
import com.ityongman.service.ITradeService;

@MyService
public class TradeService implements ITradeService {
    @Override
    public String query(String tid) {
        return "TradeService request param is " + tid;
    }
}
