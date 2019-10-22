package com.ityongman.design.singleton.lazy;

/**
 * 1. 构造方法私有
 * 2. 提供一个待创建的变量
 * 3. 提供一个访问实例对象的全局方法(static)
 */
public class LazySynchronizeSigleton {
    //1.
    private LazySynchronizeSigleton(){}

    //2.
    private static LazySynchronizeSigleton singleton = null ;

    //3.
    public static synchronized LazySynchronizeSigleton getSingleton(){
        if (null == singleton) {
            singleton = new LazySynchronizeSigleton();
        }

        return singleton ;
    }

}
