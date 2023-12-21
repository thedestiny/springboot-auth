package com.platform.productserver;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 模板生成器，用于添加全局变量
 * @Description
 * @Author kaiyang
 * @Date 2023-12-19 5:26 下午
 */

@Data
public class AppEngine extends FreemarkerTemplateEngine {

    private Map<String,Object> parameters;

    @Override
    public Map<String, Object> getObjectMap(TableInfo tableInfo) {
        Map<String, Object> objectMap = super.getObjectMap(tableInfo);
        for(Map.Entry<String, Object> entry : parameters.entrySet()){
            objectMap.put(entry.getKey(), entry.getValue());
        }
        return objectMap;
    }
}
