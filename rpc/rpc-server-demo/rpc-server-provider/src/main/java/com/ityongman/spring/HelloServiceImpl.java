package com.ityongman.spring;

import com.ityongman.service.IHelloService;
import com.ityongman.spring.annotation.RpcService;

/**
 * @Author shedunze
 * @Date 2020-02-02 13:19
 * @Description
 */
@RpcService(value = IHelloService.class, version = "1.0")
public class HelloServiceImpl implements com.ityongman.service.IHelloService {
    @Override
    public String sayHello(String content) {
        System.out.println("1.0 --> " + content);
        return "1.0 sayHello --> " + content;
    }
}
