package com.platform.authmpg.config;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.date.DateUtil;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-06-06 4:24 PM
 */

@Slf4j
public class AppJsonSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date date, JsonGenerator generator, SerializerProvider provider) throws IOException {

        if (date == null) {
            generator.writeNull();
        } else {
            generator.writeString(DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
        }

    }
}

