package com.ityongman.second.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author shedunze
 * @Date 2020-03-09 11:24
 * @Description 用于熟悉 @SpringBootApplication 注解中 @Import 注解
 */
@ComponentScan(value = "com.ityongman.second.main")
public class MainApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(MainApplication.class);


        System.out.println("--------------------");
        String[] beanNames = context.getBeanDefinitionNames();
        for (int i = 0 ; i < beanNames.length ; i++) {
            System.out.println(beanNames[i]);
        }
    }
}
