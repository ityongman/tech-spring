package com.ityongman.framework.factory;

/**
 * @Author shedunze
 * @Date 2020-01-03 10:12
 * @Description
 */
public interface BeanFactory {
    //根据bean的名字，获取在IOC容器中得到bean实例
    Object getBean(String name) throws Exception;

    //根据beanClass信息，获取在IOC容器中得到bean实例
    Object getBean(Class<?> beanClass) throws Exception;
}
