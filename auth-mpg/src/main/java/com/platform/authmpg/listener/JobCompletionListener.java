package com.platform.authmpg.listener;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

import java.util.jar.JarEntry;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-11-18 6:38 PM
 */

@Slf4j
@Data
public class JobCompletionListener extends JobExecutionListenerSupport {


    @Override
    public void afterJob(JobExecution jobExecution) {
        // 项目完成以后调用
        if(jobExecution.getStatus() == BatchStatus.COMPLETED){
            System.out.println("项目已经完成");
        }
    }
}
