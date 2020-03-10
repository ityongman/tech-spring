package com.ityongman.starter.configuration;

import com.ityongman.starter.format.AddressProcessor;
import com.ityongman.starter.format.BaseAddressProcessor;
import com.ityongman.starter.format.FastjsonAddressProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Author shedunze
 * @Date 2020-03-09 16:37
 * @Description 序列化相关配置
 */
@Configuration
public class SerialiConfigurazed {
    @Primary
    @Bean
    @ConditionalOnMissingClass("com.alibaba.fastjson.JSON")
    public AddressProcessor baseProcessor() {
        return new BaseAddressProcessor() ;
    }

    @Bean
    @ConditionalOnClass(name = "com.alibaba.fastjson.JSON")
    public AddressProcessor fastJsonProcessor() {
        return new FastjsonAddressProcessor();
    }
}
