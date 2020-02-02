package com.ityongman.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author shedunze
 * @Date 2020-02-02 17:30
 * @Description
 */
@Configuration
public class SpringConfig {

    @Bean
    public RpcProxyClient getRpcProxyClient() {
        return new RpcProxyClient() ;
    }
}
