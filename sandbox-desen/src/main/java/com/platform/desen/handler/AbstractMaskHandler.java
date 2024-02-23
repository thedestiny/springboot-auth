package com.platform.desen.handler;


import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-02-23 10:04 AM
 */
public abstract class AbstractMaskHandler implements MaskHandler {


    public String matcher(String str, Pattern pattern) {
        if (StrUtil.isBlank(str)) {
            return str;
        } else {
            Matcher matcher = pattern.matcher(str);
            StringBuffer sb = new StringBuffer();

            while(matcher.find()) {
                String group = matcher.group();
                String lastReplace = lastReplace(group, getStartIdx(group), getEndIdx(group));
                matcher.appendReplacement(sb, lastReplace);
            }

            matcher.appendTail(sb);
            return sb.toString();
        }
    }

    public abstract int getStartIdx(String matcherGroupStr);

    public abstract int getEndIdx(String matcherGroupStr);

    public String lastReplace(String str, int startLength, int endLength) {
        String s = StringUtils.leftPad(StringUtils.right(str, endLength), str.length() - startLength, "*");
        return StrUtil.isBlank(str) ? "" : StringUtils.left(str, startLength).concat(s);
    }

    public abstract String regrex(String str);

    public abstract String keyword(String str);
}
