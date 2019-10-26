package com.ityongman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RedisCachingApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisCachingApplication.class, args);
    }

}
