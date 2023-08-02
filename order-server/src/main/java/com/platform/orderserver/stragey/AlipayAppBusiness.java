package com.platform.orderserver.stragey;

import com.platform.orderserver.dto.PayDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author liangwenchao
 * @Date 2023-08-02 4:36 PM
 */

@Slf4j
@Service
public class AlipayAppBusiness extends AbstractAppBusiness implements BaseBusiness {


    @Override
    protected boolean doPay(PayDto pay) {
        return false;
    }

    @Override
    public boolean sendEmail(PayDto pay) {
        return super.sendEmail(pay);
    }
}
