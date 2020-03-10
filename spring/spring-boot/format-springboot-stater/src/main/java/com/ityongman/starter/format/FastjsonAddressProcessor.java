package com.ityongman.starter.format;

import com.alibaba.fastjson.JSON;

/**
 * @Author shedunze
 * @Date 2020-03-09 16:45
 * @Description FastJson 序列化方式
 */
public class FastjsonAddressProcessor implements AddressProcessor {
    @Override
    public <T> String format(T obj) {
        return "FastjsonAddressProcessor: " +JSON.toJSONString(obj);
    }
}
