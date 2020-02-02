package com.ityongman.spring.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author shedunze
 * @Date 2020-02-02 16:02
 * @Description
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component // spring 扫描
public @interface RpcService {
    Class<?> value() ;

    /**
     * 版本号
     * @return
     */
    String version() default "" ;
}
