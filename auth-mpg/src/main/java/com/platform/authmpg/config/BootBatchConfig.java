package com.platform.authmpg.config;

import com.platform.authmpg.listener.JobCompletionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-11-18 5:49 PM
 */


@Slf4j
@Component
public class BootBatchConfig {


    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job processJob() {
        return jobBuilderFactory.get("processJob")
                .incrementer(new RunIdIncrementer())
                .listener(null)
                .flow(null)
//                .listener(listener())
//                .flow(orderStep1())
                .end()
                .build();
    }


    @Bean
    // 步骤1 bean 先读再写
    public Step orderStep1() {
        return stepBuilderFactory.get("orderStep1").<String, String> chunk(1)
                .reader(new AppReader())
                .processor(new AppProcessor())
                .writer(new AppWriter())    // 读取。处理
                .build();  // 最后写
    }

    @Bean
    public JobExecutionListener listener() {
        return new JobCompletionListener(); // 创建监听
    }



}
