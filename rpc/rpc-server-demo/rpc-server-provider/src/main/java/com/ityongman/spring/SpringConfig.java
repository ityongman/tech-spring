package com.ityongman.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author shedunze
 * @Date 2020-02-02 16:59
 * @Description
 */
@Configuration
@ComponentScan(basePackages = "com.ityongman.spring")
public class SpringConfig {

    @Bean("rpcServer")
    public RpcServer getRpcserver() {
        return new RpcServer(8088);
    }
}
