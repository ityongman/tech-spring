package com.ityongman.design.singleton.seriable;

import java.io.Serializable;

/**
 * 饿汉式单例
 * 反序列化创建对象
 * 1. 将已经持久化的字节码内容, 转换为IO流
 * 2. 通过对iO 流的读取, 进而将读取的内容转为Java对象
 * 3. 在转换过程中会重新创建对象
 */
public class SeriableSingleton implements Serializable {
    private SeriableSingleton () {}

    private final static SeriableSingleton singleton = new SeriableSingleton();

    public static SeriableSingleton getSingleton() {
        return singleton ;
    }

    // 避免序列化问题
//    private Object readResolve(){
//        return singleton ;
//    }
}
