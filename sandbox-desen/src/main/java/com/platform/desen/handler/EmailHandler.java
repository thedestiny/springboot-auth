package com.platform.desen.handler;

import cn.hutool.core.util.DesensitizedUtil;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-02-22 6:35 PM
 */

@Component
public class EmailHandler extends AbstractMaskHandler implements MaskHandler{

    private static final Pattern PATTERN = Pattern.compile("([\\w]+(\\.[\\w]+)*@[\\w]+(\\.[\\w])+)");

    public int getStartIdx(String matcherGroupStr) {
        return 1;
    }

    public int getEndIdx(String matcherGroupStr) {
        return matcherGroupStr.length() - matcherGroupStr.indexOf("@");
    }

    public String regrex(String str) {
            return this.matcher(str, PATTERN);
    }

    public String keyword(String str) {
        return DesensitizedUtil.email(str);
    }



}
