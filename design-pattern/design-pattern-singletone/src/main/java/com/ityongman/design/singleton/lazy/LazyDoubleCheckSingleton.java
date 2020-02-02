package com.ityongman.design.singleton.lazy;

/**
 * 1. 构造方法私有
 * 2. 提供一个待创建的变量
 * 3. 提供一个访问实例对象的全局方法(static)
 *
 * - 分配内存给这个对象
 * - 初始化对象
 * - 将初始化好的对象和内存地址建立关联, 赋值
 * - 用户初次访问
 */
public class LazyDoubleCheckSingleton {
    //1.
    private LazyDoubleCheckSingleton(){}

    //2.
    private static LazyDoubleCheckSingleton singleton = null ;

    //3.
    public static LazyDoubleCheckSingleton getSingleton(){
        if (null == singleton) {
            synchronized (LazySimpleSingleton.class) {
                if (null == singleton) {
                    singleton = new LazyDoubleCheckSingleton();
                }
            }

        }

        return singleton ;
    }
}
