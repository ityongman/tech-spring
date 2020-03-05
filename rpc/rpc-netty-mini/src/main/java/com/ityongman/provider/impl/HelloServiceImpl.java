package com.ityongman.provider.impl;

import com.ityongman.api.IHelloService;

/**
 * @Author shedunze
 * @Date 2020-03-02 15:40
 * @Description helloService 实现类
 */
public class HelloServiceImpl implements IHelloService {
    @Override
    public String hello(String msg) {
        return "Hello " + msg ;
    }
}
