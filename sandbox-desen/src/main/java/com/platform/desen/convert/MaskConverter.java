package com.platform.desen.convert;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.platform.desen.config.MaskConfig;
import com.platform.desen.utils.KeywordMaskUtils;
import com.platform.desen.utils.RegrexMaskUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-02-22 6:38 PM
 */
@Slf4j
public class MaskConverter extends MessageConverter {

    @Override
    public String convert(ILoggingEvent event) {
        String origLog = event.getFormattedMessage();

        try {
            MaskConfig config = SpringUtil.getBean("maskConfig", MaskConfig.class);
            if (ObjectUtil.isEmpty(config)) {
                return origLog;
            } else if (!config.getEnable()) {
                return origLog;
            } else if (!StrUtil.startWith(event.getLoggerName(), config.getPath())) {
                return origLog;
            } else {

                KeywordMaskUtils b1 = SpringUtil.getBean(KeywordMaskUtils.class);
                RegrexMaskUtils b2 = SpringUtil.getBean(RegrexMaskUtils.class);

                String maskLog = origLog;
                // 关键字方式
                if(config.getKeyword() && config.getKeywordMap() != null && b1 != null){
                     return b1.doMask(maskLog);
                }
                // 正则方式
                if(config.getRegrex() && config.getRegrexMap() != null && b2 != null){
                    return b2.doMask(maskLog);
                }
            }

        } catch (Exception e) {
            log.info("error is {}", e.getMessage(), e);
        }
        return origLog;
    }


}
