package com.ityongman.starter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @Author shedunze
 * @Date 2020-03-09 16:35
 * @Description country配置相关信息
 */

/**
 * 加载配置文件固定头节点的方式如下
 * 1. application.properties 格式如仙 {固定前缀}.{变量名}.{map的key}={map的value}
 *      ityongman.format.address
 * 2. 除了使用变量方式, 通过set方法进行注入, 也可以通过单纯的字段进行注入, 如下
 *      private String city ; --> ityongman.format.city=hangzhou
 *
 * 如果不符合上面的格式, 信息将不会正确的注入, 者符合Spring boot 的约定大于配置的规范
 */
@ConfigurationProperties(prefix = AddressProperties.PREFIX)
public class AddressProperties {
    /**
     * 加载的文件的前缀
     */
    public static final String PREFIX = "ityongman.format" ;

    private Map<String, Object> address ;

    public Map<String, Object> getAddress() {
        return address;
    }

    public void setAddress(Map<String, Object> address) {
        this.address = address;
    }
}
