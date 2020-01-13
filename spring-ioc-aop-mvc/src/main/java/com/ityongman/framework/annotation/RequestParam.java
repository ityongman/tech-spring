package com.ityongman.framework.annotation;

import java.lang.annotation.*;

/**
 * @Author shedunze
 * @Date 2020-01-03 10:01
 * @Description 请求参数 注解
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value() default "";
}
