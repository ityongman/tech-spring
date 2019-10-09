package com.ityongman.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis/pubsub")
public class PubSubController {
    @Autowired
    private StringRedisTemplate redisTemplate ;

    @RequestMapping("publish") // get message
    public String publish(String message){
        redisTemplate.convertAndSend("string-topic", message);

        return "SUCCESS" ;
    }

    @RequestMapping("publish2") // can not get message
    public String publish2(String message){
        redisTemplate.convertAndSend("string-topic2", message);

        return "SUCCESS" ;
    }

}
