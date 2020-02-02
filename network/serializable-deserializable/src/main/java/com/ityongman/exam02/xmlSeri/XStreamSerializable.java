package com.ityongman.exam02.xmlSeri;

import com.ityongman.ISerializer;
import com.thoughtworks.xstream.XStream;

/**
 * @Author shedunze
 * @Date 2020-02-01 16:25
 * @Description
 */
public class XStreamSerializable implements ISerializer {
    /**
     *
     <com.ityongman.model.User serialization="custom">
         <com.ityongman.model.User>
             <default>
                 <age>24</age>
                 <userName>tom</userName>
             </default>
             <string>tom</string>
         </com.ityongman.model.User>
     </com.ityongman.model.User>
     */
    @Override
    public <T> byte[] serialize(T obj) {
        XStream xStream = new XStream();
        System.out.println(xStream.toXML(obj));
        return xStream.toXML(obj).getBytes() ;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        XStream xStream = new XStream();

        return (T) xStream.fromXML(new String(bytes));
    }
}
