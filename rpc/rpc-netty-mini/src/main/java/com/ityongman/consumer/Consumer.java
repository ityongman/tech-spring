package com.ityongman.consumer;

import com.ityongman.api.ICalculateService;
import com.ityongman.api.IHelloService;

/**
 * @Author shedunze
 * @Date 2020-03-03 09:07
 * @Description 消费者代码
 */
public class Consumer {
    public static void main(String[] args) {
        //1. helloService
        IHelloService helloService = RpcProxy.create(IHelloService.class);
        System.out.println(helloService.hello("netty"));

        //2. calculateService
        ICalculateService calculateService = RpcProxy.create(ICalculateService.class) ;
        System.out.println(calculateService.add(8,4));
        System.out.println(calculateService.sub(8,4));
        System.out.println(calculateService.mult(8,4));
        System.out.println(calculateService.div(8,4));

    }
}
