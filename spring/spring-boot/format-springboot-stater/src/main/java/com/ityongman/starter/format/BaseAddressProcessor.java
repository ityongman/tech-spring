package com.ityongman.starter.format;

/**
 * @Author shedunze
 * @Date 2020-03-09 16:46
 * @Description 基本序列化方式
 */
public class BaseAddressProcessor implements AddressProcessor {
    @Override
    public <T> String format(T obj) {
        return "BaseAddressProcessor: "  + obj.toString();
    }
}
