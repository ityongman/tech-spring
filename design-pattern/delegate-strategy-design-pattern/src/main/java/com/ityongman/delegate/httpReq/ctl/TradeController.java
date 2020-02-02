package com.ityongman.delegate.httpReq.ctl;

public class TradeController {
    //RequestMapping("queryOrderById")
    public String queryOrderById(String id) {
        return "TradeController queryOrderById --> " + id ;
    }
}
