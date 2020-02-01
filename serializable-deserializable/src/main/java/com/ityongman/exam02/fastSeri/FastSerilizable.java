package com.ityongman.exam02.fastSeri;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ityongman.ISerializer;

/**
 * @Author shedunze
 * @Date 2020-02-01 16:05
 * @Description
 */
public class FastSerilizable implements ISerializer {
    @Override
    public <T> byte[] serialize(T obj) {
        return JSON.toJSONString(obj).getBytes();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return JSONObject.parseObject(bytes, clazz);
    }
}
