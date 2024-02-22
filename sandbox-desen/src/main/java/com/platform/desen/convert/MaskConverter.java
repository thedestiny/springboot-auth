package com.platform.desen.convert;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
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
        log.info(origLog);
        return super.convert(event);
    }


}
