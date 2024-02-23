package com.platform.desen.handler;

import cn.hutool.core.util.DesensitizedUtil;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-02-23 4:23 PM
 */

@Component
public class IdCardHandler extends AbstractMaskHandler implements MaskHandler {


    public int getStartIdx(String matcherGroupStr) {
        return 0;
    }

    public int getEndIdx(String matcherGroupStr) {
        return 0;
    }

    public String regrex(String str) {
        return null;
    }

    public String keyword(String str) {
        return DesensitizedUtil.idCardNum(str,6 , 4);
    }
}
