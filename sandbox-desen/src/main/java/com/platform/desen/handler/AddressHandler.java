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
public class AddressHandler extends AbstractMaskHandler implements MaskHandler {

    private static final Pattern PATTERN = Pattern.compile("\\d+");


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
        return DesensitizedUtil.address(str, str.length() / 2);
    }


}
