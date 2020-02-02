package com.ityongman.controller;

import com.alibaba.fastjson.JSON;
import com.ityongman.entity.eo.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping("query/info")
    public String queryUserInfo(@RequestBody List<User> users) {
//        User user = new User();
//        user.setUserName("Bob");
//        user.setAge(25);

        return JSON.toJSONString(users);
    }
}
