package com.platform.orderserver.stragey;

import com.platform.authcommon.common.Result;
import com.platform.orderserver.dto.PayDto;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2023-08-02 4:33 PM
 */

@Slf4j
public abstract class AbstractAppBusiness implements BaseBusiness {


    @Override
    public Result<String> handleOrderFlow(PayDto pay) {

        // step1 支付
        boolean result = doPay(pay);
        if (!result) {
            return Result.failed("支付失败!");
        }
        // step2 发送短信和邮件通知到客户
        sendEmail(pay);
        sendPhone(pay);




        return Result.success("处理成功!");
    }

    // 订单支付
    protected abstract boolean doPay(PayDto pay);


    public boolean sendEmail(PayDto pay) {
        log.info("send email for order {}", pay.getOrderNo());
        return true;
    }
    public boolean sendPhone(PayDto pay) {
        log.info("send phone for order {}", pay.getOrderNo());
        return true;
    }

}
