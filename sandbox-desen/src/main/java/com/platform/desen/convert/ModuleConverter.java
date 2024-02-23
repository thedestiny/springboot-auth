package com.platform.desen.convert;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import cn.hutool.core.net.NetUtil;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-02-23 9:12 AM
 */
public class ModuleConverter extends ClassicConverter {

    public String convert(ILoggingEvent event) {
        return event.getLoggerName().length() > 20 ? "" : event.getLoggerName();
    }


}
