package com.ityongman.delegate.httpReq.ctl;

public class UserController {
    //RequestMapping("queryUserById")
    public String queryUserById(String id) {
        return "UserController queryUserById --> " + id ;
    }
}
