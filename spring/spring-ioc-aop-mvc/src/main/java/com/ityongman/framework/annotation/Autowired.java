package com.ityongman.framework.annotation;

import java.lang.annotation.*;

/**
 * @Author shedunze
 * @Date 2020-01-03 09:45
 * @Description 自动编织
 * 标记一个构造方法、字段、setter方法或配置方法 作为Spring依赖注入的对象
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    boolean required() default true;
}
