package com.ityongman.annotation;

import java.lang.annotation.*;

/**
 * 定义一个和 @RequestMapping 注解, 一样功能的注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestMapping {
    String value() default "";
}
