package com.ityongman.third;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

/**
 * @Author shedunze
 * @Date 2020-03-09 11:38
 * @Description 实现了 ImportBeanDefinitionRegistrar 的类 , Spring boot 在启动的时候
 *      会自动加载这个类
 */
public class ThirdImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        Class<ThirdTwoClass> aClass = ThirdTwoClass.class;

        /**
         * 注意和 ThirdImportSelector 类处理的不同
         */
        String beanName = StringUtils.uncapitalize(aClass.getSimpleName());
        RootBeanDefinition root = new RootBeanDefinition(aClass);

        /**
         * param1 --> beanName
         * param2 --> RootBeanDefinition
         */
        registry.registerBeanDefinition(beanName, root);
    }
}
