package com.platform.productserver.task;

import com.platform.productserver.business.RedPkgBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Description 红包定时任务处理
 * @Date 2023-08-29 4:45 PM
 */
@Slf4j
@Component
public class RedPkgTask {


    @Autowired
    private RedPkgBusiness business;

    /**
     * 处理红包超时任务
     */
    @Scheduled(cron = "30 1/2 * * * ?")
    public void task() {
        log.info("红包超时定时任务处理");
        business.handleRedPkgOverTime();
    }



}
