package com.ruoyi.common.utils;

import java.util.List;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;

/**
 * JSON工具类（基于FastJSON2）
 *
 * @author ruoyi
 */
public class JsonUtils
{
    /**
     * 对象转JSON字符串
     */
    public static String toJsonString(Object object)
    {
        return JSON.toJSONString(object);
    }

    /**
     * 对象转JSON字符串（格式化输出）
     */
    public static String toPrettyJsonString(Object object)
    {
        return JSON.toJSONString(object, JSONWriter.Feature.PrettyFormat);
    }

    /**
     * JSON字符串转对象
     */
    public static <T> T parseObject(String json, Class<T> clazz)
    {
        return JSON.parseObject(json, clazz);
    }

    /**
     * JSON字符串转对象（支持泛型类型，如 List<User> 使用 new TypeReference<List<User>>(){})
     */
    public static <T> T parseObject(String json, com.alibaba.fastjson2.TypeReference<T> typeReference)
    {
        return JSON.parseObject(json, typeReference);
    }

    /**
     * JSON字符串转JSONObject
     */
    public static JSONObject parseObject(String json)
    {
        return JSON.parseObject(json);
    }

    /**
     * JSON字符串转数组
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz)
    {
        return JSON.parseArray(json, clazz);
    }

    /**
     * JSON字符串转JSONArray
     */
    public static JSONArray parseArray(String json)
    {
        return JSON.parseArray(json);
    }

    /**
     * 对象转JSON字节数组
     */
    public static byte[] toJsonBytes(Object object)
    {
        return JSON.toJSONBytes(object);
    }

    /**
     * JSON字节数组转对象
     */
    public static <T> T parseObject(byte[] bytes, Class<T> clazz)
    {
        return JSON.parseObject(bytes, clazz);
    }

    /**
     * 判断字符串是否为合法JSON
     */
    public static boolean isValidJson(String json)
    {
        try
        {
            JSON.parse(json);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * 深拷贝对象（通过JSON序列化与反序列化实现）
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(T object)
    {
        return (T) JSON.parseObject(JSON.toJSONBytes(object), object.getClass());
    }
}
