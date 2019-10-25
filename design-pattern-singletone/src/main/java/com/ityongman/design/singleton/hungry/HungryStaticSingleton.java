package com.ityongman.design.singleton.hungry;

/**
 * 1. 构造方法私有
 * 2. 提供一个已经创建好的实例对象
 * 3. 提供一个访问实例对象的全局方法
 */
public class HungryStaticSingleton {

    //1.
    private HungryStaticSingleton() {}

    //2.
    private static final HungryStaticSingleton instance ;

    static {
        instance = new HungryStaticSingleton();
    }

    //3.
    public HungryStaticSingleton getInstance() {
        return instance ;
    }
}
