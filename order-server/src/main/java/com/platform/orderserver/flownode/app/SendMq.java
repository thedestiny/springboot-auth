package com.platform.orderserver.flownode.app;

import cn.hutool.core.util.RandomUtil;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 发送mq 业务
 */
@Slf4j
@Component(value = "sendMq")
public class SendMq extends NodeComponent {
    @Override
    public void process() throws Exception {
        TimeUnit.SECONDS.sleep(RandomUtil.randomInt(0, 20));
        log.info("handle send mq !");

    }
}
