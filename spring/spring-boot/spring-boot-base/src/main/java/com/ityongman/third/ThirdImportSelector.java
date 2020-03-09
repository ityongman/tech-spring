package com.ityongman.third;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;
import java.util.Set;

/**
 * @Author shedunze
 * @Date 2020-03-09 11:34
 * @Description 实现了 ImportSelector 的类, Spring boot 在启动的时候会自动加载到 Bean容器中
 */
public class ThirdImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
//        Class<ThirdOneClass> clazz = ThirdOneClass.class;
//        System.out.println(clazz.getName());
//        System.out.println(clazz.getSimpleName());

        /**
         * String[] 中存储的是 class 的全路径信息, 不能是类的简单名称
         *
         * 1. ThirdOneClass.class.getName() com.ityongman.third.ThirdOneClass
         * 2. ThirdOneClass.class.getSimpleName() ThirdOneClass 这中会报错
         */
        return new String[]{ThirdOneClass.class.getName()};
    }

}
