package com.goodcol.muses.utils;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Json转换工具类
 *
 * @author Mr.kusch
 * @date 2021/6/9 22:20
 */
public class JsonUtils {
    private static ObjectMapper mapper = new ObjectMapper();

    static {
        // 解决实体未包含字段反序列化时抛出异常
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 对于空的对象转json的时候不抛出错误
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // 允许属性名称没有引号
        mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        // 允许单引号
        mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);

        // 忽略在json字符串中存在,但是在java对象中不存在对应属性的情况
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 处理时间转换问题
        mapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    }

    /**
     * 将一个object转换为json
     *
     * @param obj
     * @return java.lang.String
     * @author Mr.kusch
     * @date 2021/6/9 22:24
     */
    public static String objectToJson(Object obj) {
        String json = null;
        try {
            json = mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 将json数据转换成Map
     *
     * @param json
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author Mr.kusch
     * @date 2021/6/9 22:24
     */
    public static Map<String, Object> jsonToMap(String json) {
        Map<String, Object> map = null;
        try {
            map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 将json数据转换成list
     *
     * @param json
     * @param beanType
     * @return java.util.List<T>
     * @author Mr.kusch
     * @date 2021/6/9 22:25
     */
    public static <T> List<T> jsonToList(String json, Class<T> beanType) {
        List<T> list = null;
        try {
            JavaType javaType =
                    mapper.getTypeFactory().constructParametricType(List.class, beanType);
            list = mapper.readValue(json, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取json对象数据的属性
     *
     * @param resData 请求的数据
     * @param resPro  请求的属性
     * @return java.lang.String
     * @author Mr.kusch
     * @date 2021/6/9 22:26
     */
    public static String findValue(String resData, String resPro) {
        String result = null;
        try {
            JsonNode node = mapper.readTree(resData);
            JsonNode resProNode = node.get(resPro);
            result = JsonUtils.objectToJson(resProNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将对象转成json格式
     *
     * @param data
     * @return java.lang.String
     * @author Mr.kusch
     * @date 2021/6/9 22:27
     */
    public static String jsonString(Object data) {
        if (null == data) {
            return null;
        }
        String json = null;
        try {
            json = mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 原实体类List转换为目标实体类List
     *
     * @param srcList 原来的List
     * @param clz     要转换的实体类class
     * @return java.util.List<T>
     * @author Mr.kusch
     * @date 2021/6/9 22:28
     */
    public static <T> List<T> srcList2ObjList(List<?> srcList, Class<T> clz) {
        String s = jsonString(srcList);
        if (s == null) {
            return new ArrayList<>();
        }
        return jsonToList(s, clz);
    }

    /**
     * 将json转成特定的cls的对象
     *
     * @param json
     * @param cls
     * @return T
     * @author Mr.kusch
     * @date 2021/6/9 22:29
     */
    public static <T> T jsonToBean(String json, Class<T> cls) {
        T t = null;
        try {
            t = mapper.readValue(json, cls);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 原实体类转换为目标实体类
     *
     * @param src 原来的实体类
     * @param clz 目标实体类
     * @return T
     * @author Mr.kusch
     * @date 2021/6/9 22:31
     */
    public static <T> T src2Obj(Object src, Class<T> clz) {
        String s = jsonString(src);
        if (s == null) {
            return null;
        }
        return jsonToBean(s, clz);
    }
}
