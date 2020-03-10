package com.ityongman.starter.configuration;

import com.AddressFormat;
import com.ityongman.starter.format.AddressProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

/**
 * @Author shedunze
 * @Date 2020-03-09 16:38
 * @Description 格式化配置相关信息
 */
@Import(value = SerialiConfigurazed.class)
@EnableConfigurationProperties({AddressProperties.class})
@Configuration
public class FormatConfiguration {

    @Bean
    public AddressFormat addressFormat(AddressProperties addressProperties, AddressProcessor processor) {
        return new AddressFormat(addressProperties, processor) ;
    }
}
