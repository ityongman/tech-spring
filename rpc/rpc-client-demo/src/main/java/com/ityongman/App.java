package com.ityongman;

import com.ityongman.service.IHelloService;
import com.ityongman.service.RpcProxyClient;
import com.ityongman.service.SpringConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Hello world!
 *[
 */
public class App {
    public static void main( String[] args ) throws InterruptedException {
        //1. base 传统方式
//        RpcProxyClient proxyClient = new RpcProxyClient();
//        IHelloService helloService = proxyClient.clientProxy(IHelloService.class, "127.0.0.1", 8080);
//
//        String result = helloService.sayHello("sayHello") ;
//        System.out.println(result);

        //2. spring
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class) ;
        ((AnnotationConfigApplicationContext)context).start();
        RpcProxyClient proxyClient = context.getBean(RpcProxyClient.class);

        IHelloService helloService = proxyClient.clientProxy(IHelloService.class, "2.0");

        /**
         * 1. 模拟负载均衡
         * 2. 模拟带有注册中心的服务调用
         */
        for(int i=0;i<100;i++) {
            Thread.sleep(100);
            System.out.println(i + " : " + helloService.sayHello("sayHello " + i));
        }
    }
}
