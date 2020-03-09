package com.ityongman.second.main;

import com.ityongman.second.other.SecondOtherConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Author shedunze
 * @Date 2020-03-09 11:27
 * @Description
 */
@Configuration
//通过开启关闭 下面的注解, 观察 @Import 注解的作用
@Import(value = SecondOtherConfiguration.class)
public class SecondConfiguration {

    @Bean
    public SecondClass secondClass() {
        return new SecondClass() ;
    }
}
