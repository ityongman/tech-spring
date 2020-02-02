package com.ityongman.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

@Deprecated
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {
    private static final String ENCODE_DECODE_CHARSET = "UTF-8" ;
    private static final Charset DEFAULT_CHARSET = Charset.forName(ENCODE_DECODE_CHARSET);
    private Class<T> clazz ;

    public FastJsonRedisSerializer(Class clazz) {
        super();
        this.clazz = clazz ;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (null == t) {
            return new byte[0] ;
        }
        return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET) ;
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if(null == bytes || bytes.length <= 0) {
            return null ;
        }

        String decodeStr = new String(bytes, DEFAULT_CHARSET);
        return JSON.parseObject(decodeStr, clazz);
    }
}
