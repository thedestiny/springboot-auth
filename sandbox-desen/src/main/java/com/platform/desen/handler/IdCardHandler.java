package com.platform.desen.handler;

import cn.hutool.core.util.DesensitizedUtil;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-02-23 4:23 PM
 */

@Component
public class IdCardHandler extends AbstractMaskHandler implements MaskHandler {

    private static final Pattern PATTERN = Pattern.compile("(?<!\\w)(([1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx])|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}))(?!\\w)");


    public int getStartIdx(String matcherGroupStr) {
        return 2;
    }

    public int getEndIdx(String matcherGroupStr) {
        return 2;
    }

    public String regrex(String str) {
        return this.matcher(str, PATTERN);
    }

    public String keyword(String str) {
        return DesensitizedUtil.idCardNum(str,6 , 4);
    }
}
