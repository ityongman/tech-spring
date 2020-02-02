package com.ityongman;

import com.ityongman.spring.SpringConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args )    {

//        //1. 基本rpc通信方式
//        IHelloService helloService = new HelloServiceImpl();
//
//        RpcProxyServer proxyServer = new RpcProxyServer() ;
//        proxyServer.publish(helloService, 8080);

        //2. spring rpc通信方式
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class) ;

        ((AnnotationConfigApplicationContext)context).start();
    }
}
