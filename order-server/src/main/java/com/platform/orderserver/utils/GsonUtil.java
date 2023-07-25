package com.platform.orderserver.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class GsonUtil {

    public static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public static String toJsonString(Object obj) {
        return gson.toJson(obj);
    }

    public static Map<String, Object> fromJson2Map(String obj) {
        TypeToken<Map<String, Object>> mapTypeToken = new TypeToken<Map<String, Object>>() {
        };
        return fromJson2Type(obj, mapTypeToken);
    }

    public static <T> T fromJson2Type(String obj, TypeToken typeToken) {
        return gson.fromJson(obj, typeToken.getType());
    }
}
