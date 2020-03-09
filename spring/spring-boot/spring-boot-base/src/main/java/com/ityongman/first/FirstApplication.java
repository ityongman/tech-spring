package com.ityongman.first;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author shedunze
 * @Date 2020-03-09 11:07
 * @Description springboot 第一个样例, 用于了解 @ComponentScan 注解
 */

/**
 * 符合下面要求的类会被扫描道
 * 1. @Configuration
 * 2. 实现了 ImportSelector 接口的类
 * 3. 实现了 ImportBeanDefinitionRegistrar 接口的类
 */

/**
 * 相当于 xml文件中配置参数 <context:component-scan>
 */
@ComponentScan(value = "com.ityongman.first")
public class FirstApplication {
    public static void main(String[] args) {
        //形参 FirstApplication.class 注解加载默认的路径和FirstApplicaiton保持同一个路径
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(FirstApplication.class) ;

        System.out.println("-------------");
        String[] beanNames = context.getBeanDefinitionNames();
        for (int i = 0 ; i < beanNames.length ; i++) {
            System.out.println(beanNames[i]);
        }

    }
}
