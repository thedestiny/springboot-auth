package com.platform.orderserver.stragey;

import cn.hutool.core.util.RandomUtil;
import com.platform.orderserver.dto.PayDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Description 支付宝业务
 * @Author liangkaiyang
 * @Date 2023-08-02 4:36 PM
 */

@Slf4j
@Service(value = "alipayAppBusiness")
public class AlipayAppBusiness extends AbstractAppBusiness implements BaseBusiness {


    @Override
    protected boolean doPay(PayDto pay) {
        try {
            TimeUnit.MILLISECONDS.sleep(RandomUtil.randomInt(200, 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("支付宝支付业务流程");
        return true;
    }

    @Override
    public boolean sendEmail(PayDto pay) {
        return super.sendEmail(pay);
    }

    @Override
    public boolean sendPhone(PayDto pay) {
        return super.sendPhone(pay);
    }
}
