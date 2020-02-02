package com.ityongman.annotation;

import java.lang.annotation.*;

/**
 * 定义一个和 @RequestParam 注解, 一样功能的注解
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestParam {
    String value() default "";
}
