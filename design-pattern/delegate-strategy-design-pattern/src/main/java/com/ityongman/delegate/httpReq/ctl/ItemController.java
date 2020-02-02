package com.ityongman.delegate.httpReq.ctl;

public class ItemController {

    //RequestMapping("queryItemById")
    public String queryItemById(String id) {
        return "ItemController queryItemById --> " + id ;
    }
}
