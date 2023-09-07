package com.platform.productserver.utils;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description
 * @Date 2023-09-07 10:17 PM
 */

@Data
public class LruCache extends LinkedHashMap<String, String> {

    /**
     * 缓存允许的最大容量
     */
    private final int maxSize;

    public LruCache(int initialCapacity, int maxSize) {
        // accessOrder必须为true
        super(initialCapacity, 0.75f, true);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
        // 当键值对个数超过最大容量时，返回true，触发删除操作
        return size() > maxSize;
    }


}
