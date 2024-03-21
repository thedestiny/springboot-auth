package com.platform.utils;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-03-21 3:53 PM
 */
@Slf4j
public class MapsUtils {


    public static void main(String[] args) {


        Map<String, String> map1 = new HashMap<>();
        map1.put("123", "123");
        map1.put("234", "234");
        map1.put("345", "345");
        map1.put("456", "456");
        map1.put("567", "567");
        map1.put("678", "678");

        Map<String, String> map2 = new HashMap<>();
        map2.put("234", "234");
        map2.put("345", "345");
        map2.put("456", "4568");
        map2.put("567", "5679");
        map2.put("678", "678");
        map2.put("789", "789");

        // map difference 的不同内容
        MapDifference<String, String> diff = Maps.difference(map1, map2);
        // 是否相等
        boolean result = diff.areEqual();
        // 左侧多的情况
        Map<String, String> left = diff.entriesOnlyOnLeft();
        // 右侧多的情况
        Map<String, String> right = diff.entriesOnlyOnRight();
        // 相同且对平的情况
        Map<String, String> common = diff.entriesInCommon();
        log.info("result is {}", result);
        log.info("result left is {}", left);
        log.info("result right {}", right);
        log.info("result common {}", common);
        // key 相同但是 value 不同的情况
        Map<String, MapDifference.ValueDifference<String>> difference = diff.entriesDiffering();
        MapDifference.ValueDifference<String> def = difference.get("456");
        log.info("result difference {} left {} right {} ", difference, def.leftValue(), def.rightValue());


    }

}
