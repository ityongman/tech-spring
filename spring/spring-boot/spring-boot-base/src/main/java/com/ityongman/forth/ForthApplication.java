package com.ityongman.forth;

import com.ityongman.StudyClass;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @Author shedunze
 * @Date 2020-03-09 13:50
 * @Description 测试
 *  1. spring-factories Spring boot启动时默认加载的文件, 文件 key -> org.springframework.boot.autoconfigure.EnableAutoConfiguration 注解信息
 *      ConditionalOnClass注解配置 spring-factories 中配置的信息是否生效, 类似开关作用
 *  2. spring-autoconfigure-metadata.properties(类是否加载的条件文件 @ConditionalOnClass 注解条件)
 *  配置文件作用
 */
@SpringBootApplication
public class ForthApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ForthApplication.class, args);

        System.out.println(context.getBean(StudyClass.class).learn("Srping boot"));
    }
}
