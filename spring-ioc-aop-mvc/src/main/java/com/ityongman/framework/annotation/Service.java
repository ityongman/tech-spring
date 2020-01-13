package com.ityongman.framework.annotation;

import java.lang.annotation.*;

/**
 * @Author shedunze
 * @Date 2020-01-03 10:03
 * @Description 业务相关 注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String value() default "";
}