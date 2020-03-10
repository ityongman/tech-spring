package com.ityongman.starter.format;

/**
 * @Author shedunze
 * @Date 2020-03-09 16:43
 * @Description 格式化地址信息处理的配置类
 */
public interface AddressProcessor {
    /**
     * format 格式化信息的方法
     */
    <T> String format(T obj) ;
}
