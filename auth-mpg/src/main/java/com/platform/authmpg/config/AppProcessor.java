package com.platform.authmpg.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-11-18 6:32 PM
 */

@Data
@Slf4j
public class AppProcessor implements ItemProcessor<String, String> {
    @Override
    public String process(String item) throws Exception {
        return item.toUpperCase();
    }
}
