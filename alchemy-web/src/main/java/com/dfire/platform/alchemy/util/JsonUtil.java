package com.dfire.platform.alchemy.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Created by congbai on 3/14/17.
 */
public class JsonUtil {

    public static <T> T fromJson(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public static <T> T fromJson(String json, TypeReference<T> tTypeReference) {
        return JSON.parseObject(json, tTypeReference);
    }

    public static <T> String toJson(T t) {
        return JSON.toJSONString(t, SerializerFeature.DisableCircularReferenceDetect);
    }
}
