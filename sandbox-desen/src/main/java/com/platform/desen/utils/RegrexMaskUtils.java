package com.platform.desen.utils;

import cn.hutool.core.util.StrUtil;
import com.platform.desen.config.MaskConfig;
import com.platform.desen.config.MaskRuleEnum;
import com.platform.desen.handler.MaskHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description 正则表达式脱敏方式
 * @Author kaiyang
 * @Date 2024-02-23 9:54 AM
 */

@Component
public class RegrexMaskUtils {

    @Autowired
    private Map<String, MaskHandler> handlerMap;
    @Autowired
    private MaskConfig maskConfig;

    public String doMask(String maskLog) {
        String processLog = maskLog;
        Map<String, String> regrexMap = maskConfig.getRegrexMap();
        // 正则表达式
        for (Map.Entry<String, String> entry : regrexMap.entrySet()) {
            // 规则关键字以及过滤规则是否开启
            String key = entry.getKey();
            String value = entry.getValue();
            if (StrUtil.equals(value, "1")) {
                processLog = handlerMap.get(MaskRuleEnum.match(key)).regrex(processLog);
            }
        }

        return processLog;
    }
}
