package com.ityongman.exam02.hessianSeri;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.ityongman.ISerializer;
import sun.management.counter.ByteArrayCounter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Author shedunze
 * @Date 2020-02-01 16:16
 * @Description
 */
public class HessianSerilizable implements ISerializer {
    @Override
    public <T> byte[] serialize(T obj) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        try {
            HessianOutput out = new HessianOutput(bout) ;
            out.writeObject(obj);

            return bout.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        HessianInput in = new HessianInput(bin);
        try {
            return (T) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null ;
    }
}
