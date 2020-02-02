package com.ityongman.design.singleton.lazy;

import com.sun.imageio.plugins.common.LZWCompressor;

/**
 * 1. 构造方法私有
 * 2. 提供一个待创建的变量
 * 3. 提供一个访问实例对象的全局方法(static)
 */
public class LazySimpleSingleton {
    //1.
    private LazySimpleSingleton(){}

    //2.
    private static LazySimpleSingleton singleton = null ;

    public static LazySimpleSingleton getSingleton(){
        if (null == singleton) {
            singleton = new LazySimpleSingleton();
        }

        return singleton ;
    }

}
