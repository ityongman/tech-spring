package com.ityongman.framework.annotation;

import java.lang.annotation.*;

/**
 * @Author shedunze
 * @Date 2020-01-03 09:55
 * @Description 控制器注解
 * 在Web Controller 层使用的注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String value() default "";
}
