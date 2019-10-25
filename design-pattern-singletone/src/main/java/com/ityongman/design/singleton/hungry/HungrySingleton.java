package com.ityongman.design.singleton.hungry;

/**
 * 1. 构造方法私有
 * 2. 提供一个已经创建好的实例对象
 * 3. 提供一个访问实例对象的全局方法
 */
public class HungrySingleton {

    //1.
    private HungrySingleton() {}

    //2.
    private static final HungrySingleton instance = new HungrySingleton();

    //3.
    public HungrySingleton getInstance() {
        return instance ;
    }
}
