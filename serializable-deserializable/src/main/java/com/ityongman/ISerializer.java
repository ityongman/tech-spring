package com.ityongman;

import java.io.OutputStream;

/**
 * @Author shedunze
 * @Date 2020-02-01 15:12
 * @Description 序列化和反序列化通用接口
 */
public interface ISerializer {
    /**
     * serialize 序列化方法
     */
    <T> byte[] serialize(T obj) ;


    /**
     * deserialize 反序列化接口
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz) ;
}
