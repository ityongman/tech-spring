package com.ityongman.design.singleton.lazy;

public class LazyInnerClassSingleton {

    private LazyInnerClassSingleton(){}

    //1. 使用的是内部类的特性
    //2. InnerCreateSingleton 里面逻辑需要等到外面方法调用的时候才执行
    //3. JVM底层执行逻辑, 可以避免线程安全问题
    public static final LazyInnerClassSingleton getInstance(){
        return InnerCreateSingleton.singleton ;
    }

    private static class InnerCreateSingleton {
        private static final LazyInnerClassSingleton singleton = new LazyInnerClassSingleton();
    }
}
