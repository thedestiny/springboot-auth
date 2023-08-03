package com.platform.orderserver.stragey;

import com.platform.authcommon.common.Result;
import com.platform.orderserver.dto.PayDto;

/**
 * @Description 业务接口类
 * @Author liangkaiyang
 * @Date 2023-08-02 4:32 PM
 */
public interface BaseBusiness {

    /**
     * 处理业务流程
     */
    Result<String> handleOrderFlow(PayDto pay);

    // 发送邮件
    boolean sendEmail(PayDto pay);

    // 发送手机短信
    boolean sendPhone(PayDto pay);
}
