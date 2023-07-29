package com.platform.orderserver.flownode;

import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


/**
 * @Description 规则引擎执行器
 * @Date 2023-03-31 2:41 PM
 */

@Slf4j
@Component
public class FlowExecutorService {


    @Autowired
    private FlowExecutor flowExecutor;


    /**
     * 处理 交易完成后任务
     * @param flowDto
     */
    @Async(value = "getAsyncExecutor")
    public void handleApp(AppFlowDto flowDto){

        LiteflowResponse response = flowExecutor.execute2Resp("test_flow", flowDto, AppFlowContext.class);

        // 获取流程执行后的结果
        if (!response.isSuccess()) {
            Exception e = response.getCause();
            log.warn(" error is {}", e.getCause(), e);
        }

        AppFlowContext context = response.getContextBean(AppFlowContext.class);
        log.info("handleApp 执行完成后 context {}", JSONObject.toJSONString(context));
    }



}
