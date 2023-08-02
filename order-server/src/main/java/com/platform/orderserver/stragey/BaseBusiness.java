package com.platform.orderserver.stragey;

import com.platform.authcommon.common.Result;
import com.platform.orderserver.dto.PayDto;

/**
 * @Description
 * @Author liangwenchao
 * @Date 2023-08-02 4:32 PM
 */
public interface BaseBusiness {

    /**
     * 处理业务流程
     * @return
     */
    Result<String> handleOrderFlow(PayDto pay);


}
