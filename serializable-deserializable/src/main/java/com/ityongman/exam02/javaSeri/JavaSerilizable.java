package com.ityongman.exam02.javaSeri;

import com.ityongman.ISerializer;

import java.io.*;

/**
 * @Author shedunze
 * @Date 2020-02-01 15:24
 * @Description java默认的序列化方式
 */
public class JavaSerilizable implements ISerializer {
    @Override
    public <T> byte[] serialize(T obj) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            ObjectOutputStream out = new ObjectOutputStream(bout);
            out.writeObject(obj);

            return bout.toByteArray() ; // 返回序列化字节信息
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream bin = new ByteArrayInputStream(bytes) ;

        try {
            ObjectInputStream in = new ObjectInputStream(bin);
            return (T) in.readObject() ;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
