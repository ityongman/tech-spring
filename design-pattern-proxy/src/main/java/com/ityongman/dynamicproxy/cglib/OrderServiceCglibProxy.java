package com.ityongman.dynamicproxy.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class OrderServiceCglibProxy implements MethodInterceptor {

    public Object getInstance(Class<?> clazz) {
        //1. 类似于JDK Proxy中 Proxy.newProxyInstance 作用
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);

        return enhancer.create() ;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        before();

        // 子类重写了父类的方法, 所以这里调用invokerSuper
        Object invokeObj = methodProxy.invokeSuper(o, objects);

        after();
        return invokeObj;
    }

    private void after() {
        System.out.println("cglib proxy after");
    }

    private void before() {
        System.out.println("cglib proxy before");
    }


}
