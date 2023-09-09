package com.platform.productserver.grant;

import com.platform.productserver.dto.AccountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 积分分发
 * @Description
 * @Date 2023-08-30 2:29 PM
 */

@Slf4j
@Service
public class GrantBusiness {


    public boolean point(GiveReq account) {
        return true;
    }
}
