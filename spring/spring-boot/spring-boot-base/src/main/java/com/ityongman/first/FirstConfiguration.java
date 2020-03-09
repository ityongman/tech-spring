package com.ityongman.first;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author shedunze
 * @Date 2020-03-09 11:15
 * @Description
 */
@Configuration
public class FirstConfiguration {

    @Bean
    public FirstClass firstClass() {
        return new FirstClass() ;
    }
}
