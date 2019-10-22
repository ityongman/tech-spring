package com.ityongman.design.singleton.register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 容器式单例 -- 可以比较Spring 容器
 */
public class ContainerSingleton {
    //1.
    private ContainerSingleton(){}

    //2.
    private static Map<String, Object> map = new ConcurrentHashMap<String, Object>();

    //3.没有synchronized关键字修饰会有线程安全问题
    public static Object getBean(String className){
        synchronized (map) {
            if(!map.containsKey(className)) {
                Object obj = null ;
                try {
                    obj = Class.forName(className).newInstance();
                    map.put(className, obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return obj ;
            }

            return map.get(className);
        }
    }
}
