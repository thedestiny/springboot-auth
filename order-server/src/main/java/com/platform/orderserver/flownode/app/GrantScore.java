package com.platform.orderserver.flownode.app;

import cn.hutool.core.collection.CollUtil;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.platform.orderserver.flownode.AppFlowContext;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * @Description 发送积分
 * @Date 2023-03-31 2:28 PM
 */
@Slf4j
@Component(value = "grantScore")
public class GrantScore extends NodeComponent {

    @Override
    public void process() throws Exception {
        AppFlowContext context = this.getContextBean(AppFlowContext.class);
        log.info("business cxt {}", JSONObject.toJSONString(context));
        TimeUnit.SECONDS.sleep(RandomUtil.randomInt(0, 20));
        log.info("handle grant score !");

    }

    // 是否处理该节点
    @Override
    public boolean isAccess() {
        AppFlowContext context = this.getContextBean(AppFlowContext.class);
        log.info("判断是否处理该节点 cxt {}", JSONObject.toJSONString(context));
        // 根据业务判断是否处理该节点
        return Boolean.TRUE;
    }


}
