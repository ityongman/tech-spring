package com.ityongman.framework.bean.config;

/**
 * @Author shedunze
 * @Date 2020-01-03 10:32
 * @Description 存储注册信息
 * Spring中BeanDefinition是接口, 下面的😁存储在AbstractBeanDefinition
 */
public class BeanDefinition {
    private volatile Object beanClass ; // 即beanClassName
    private boolean lazyInit = false ; // 懒加载, 默认 false
    private String factoryBeanName ; // 工厂bean名称
    private boolean isSingleton = true ;

    public Object getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Object beanClass) {
        this.beanClass = beanClass;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public void setSingleton(boolean singleton) {
        isSingleton = singleton;
    }
}
