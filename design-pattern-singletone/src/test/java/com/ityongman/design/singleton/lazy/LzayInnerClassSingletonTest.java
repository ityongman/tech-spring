package com.ityongman.design.singleton.lazy;

import java.lang.reflect.Constructor;

/**
 * 针对内部类创建单例模式, 模拟反射机制暴力破解单例
 */
public class LzayInnerClassSingletonTest {
    public static void main(String[] args) {
        try {
            //1. 反射暴力破解
            Class<?> clazz = LazyInnerClassSingleton.class;
            Constructor<?> c = clazz.getDeclaredConstructor(null);
            c.setAccessible(true);
            Object o1 = c.newInstance();

            //2. 单例直接获取对象
            Object o2 = LazyInnerClassSingleton.getInstance();

            System.out.println(o1 == o2);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

