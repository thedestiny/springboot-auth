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
public class NameHandler extends AbstractMaskHandler implements MaskHandler {

    private static final Pattern PATTERN = Pattern.compile("(?<!\\w)1[3|4|5|6|7|8|9][0-9]\\d{8}(?!\\w)");

    public int getStartIdx(String matcherGroupStr) {
        return 2;
    }

    public int getEndIdx(String matcherGroupStr) {
        return 3;
    }

    public String regrex(String str) {
        return matcher(str, PATTERN);
    }

    public String keyword(String str) {
        return DesensitizedUtil.chineseName(str);
    }


}
