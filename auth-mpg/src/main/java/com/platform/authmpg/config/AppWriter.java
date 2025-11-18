package com.platform.authmpg.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * https://mp.weixin.qq.com/s/UkCresDFfHj-rtGh5xmzOg
 * @Description
 * @Author liangkaiyang
 * @Date 2025-11-18 6:33 PM
 */

@Data
@Slf4j
public class AppWriter implements ItemWriter<String> {


    @Override
    public void write(List<? extends String> list) throws Exception {
        list.forEach(item -> log.info("write item: {}", item));
    }
}
