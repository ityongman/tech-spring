package com.ityongman.second.other;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author shedunze
 * @Date 2020-03-09 11:29
 * @Description
 */
@Configuration
public class SecondOtherConfiguration {

    @Bean
    public SecondOtherClass secondOtherClass(){
        return new SecondOtherClass() ;
    }
}
