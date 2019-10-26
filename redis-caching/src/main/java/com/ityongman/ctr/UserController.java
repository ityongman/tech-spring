package com.ityongman.ctr;

import com.ityongman.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService ;

    @RequestMapping("/query/{userId}")
    public String queryUserById(@PathVariable("userId") Long userId) {
        return userService.queryUserById(userId);
    }
}
