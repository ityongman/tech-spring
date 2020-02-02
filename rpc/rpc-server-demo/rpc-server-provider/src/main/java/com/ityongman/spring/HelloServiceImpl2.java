package com.ityongman.spring;

import com.ityongman.service.IHelloService;
import com.ityongman.spring.annotation.RpcService;

/**
 * @Author shedunze
 * @Date 2020-02-02 13:19
 * @Description
 */
@RpcService(value = IHelloService.class, version = "2.0")
public class HelloServiceImpl2 implements IHelloService {
    @Override
    public String sayHello(String content) {
        System.out.println("2.0 --> " + content);
        return "2.0 sayHello --> " + content;
    }
}
