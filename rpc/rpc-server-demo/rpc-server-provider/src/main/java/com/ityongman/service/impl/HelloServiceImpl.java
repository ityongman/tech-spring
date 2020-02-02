package com.ityongman.service.impl;

/**
 * @Author shedunze
 * @Date 2020-02-02 13:19
 * @Description
 */
public class HelloServiceImpl implements com.ityongman.service.IHelloService {
    @Override
    public String sayHello(String content) {
        System.out.println("--> " + content);
        return "sayHello --> " + content;
    }
}
