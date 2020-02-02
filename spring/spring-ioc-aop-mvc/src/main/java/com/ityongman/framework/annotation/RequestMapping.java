package com.ityongman.framework.annotation;

import java.lang.annotation.*;

/**
 * @Author shedunze
 * @Date 2020-01-03 09:59
 * @Description 请求映射 注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value() default "";
}
