package com.ityongman.framework.bean.support;

import com.ityongman.framework.bean.config.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author shedunze
 * @Date 2020-01-03 10:30
 * @Description
 */
public class DefaultListableBeanFactory {
    //存储注册信息的BeanDefinition
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
}
