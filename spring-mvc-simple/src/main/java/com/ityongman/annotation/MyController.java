package com.ityongman.annotation;

import java.lang.annotation.*;

/**
 * 定义一个和 @Controller注解, 功能一样的注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyController {
    String value() default "";
}