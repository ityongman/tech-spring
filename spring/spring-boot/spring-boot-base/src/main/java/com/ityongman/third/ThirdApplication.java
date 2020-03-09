package com.ityongman.third;

/**
 * @Author shedunze
 * @Date 2020-03-09 11:32
 * @Description 通过实现 ImportSelector 、ImportBeanDefinitionRegistrar
 *      掌握 Spring 自身内部加载类的方式
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * 符合下面要求的类会被扫描道
 * 1. @Configuration
 * 2. 实现了 ImportSelector 接口的类
 * 3. 实现了 ImportBeanDefinitionRegistrar 接口的类
 */
//@ComponentScan(value = "com.ityongman.third")
@SpringBootApplication
//@EnableDefineService
public class ThirdApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext cac = SpringApplication.run(ThirdApplication.class, args);

        System.out.println("---------------");
        String[] beanNames = cac.getBeanDefinitionNames();
        for(int i=0 ; i<beanNames.length ; i++) {
            System.out.println(beanNames[i]);
        }
    }
}
