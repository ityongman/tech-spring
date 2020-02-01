package com.ityongman.exam02.javaWithFile;

import com.ityongman.ISerializer;

import java.io.*;

/**
 * @Author shedunze
 * @Date 2020-02-01 16:43
 * @Description
 */
public class JavaWithFileSerilizable implements ISerializer {
    @Override
    public <T> byte[] serialize(T obj) {
        try {
            FileOutputStream fout = new FileOutputStream(new File("userWithFile"));
            ObjectOutputStream out = new ObjectOutputStream(fout);

            out.writeObject(obj);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            FileInputStream fin = new FileInputStream(new File("userWithFile")) ;
            ObjectInputStream in = new ObjectInputStream(fin) ;

            return (T) in.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
