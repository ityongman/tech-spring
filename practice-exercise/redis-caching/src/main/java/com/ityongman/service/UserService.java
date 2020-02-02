package com.ityongman.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {

    @Cacheable(value = "cache-test")
    public String queryUserById(Long userId) {
        System.out.println("queryUserById ....");
        long waitTime = 2000L;
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "success " + userId ;
    }
}
