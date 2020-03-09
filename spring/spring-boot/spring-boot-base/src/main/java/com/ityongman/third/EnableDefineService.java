package com.ityongman.third;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author shedunze
 * @Date 2020-03-09 13:20
 * @Description 自定义注解 EnableDefineService
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({ThirdImportSelector.class, ThirdImportBeanDefinitionRegistrar.class})
public @interface EnableDefineService {
    Class<?>[] exclude() default {} ;
}
