package com.platform.orderserver.stragey;

import com.platform.authcommon.common.Result;
import com.platform.orderserver.dto.PayDto;

/**
 * 策略模式的上下文
 */
public class PayContext {


    private BaseBusiness business;

    public PayContext(BaseBusiness bus){
        this.business = bus;
    }

    //
    public boolean sendEmail(PayDto pay){
        return business.sendEmail(pay);
    }

    boolean sendPhone(PayDto pay){
        return business.sendPhone(pay);
    }
}
