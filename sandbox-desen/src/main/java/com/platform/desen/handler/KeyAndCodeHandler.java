package com.platform.desen.handler;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;


/**
 * @Description
 * @Author kaiyang
 * @Date 2024-02-23 9:04 PM
 */

@Component
public class KeyAndCodeHandler extends AbstractMaskHandler implements MaskHandler  {


    public int getStartIdx(String matcherGroupStr) {
        return 0;
    }

    public int getEndIdx(String matcherGroupStr) {
        return 0;
    }

    public String regrex(String str) {
        return str;
    }

    public String keyword(String str) {
        String s = StrUtil.blankToDefault(str, "");
        return StrUtil.length(s) > 7 ? this.lastReplace(s, 3, 3) : "*****";
    }

}
