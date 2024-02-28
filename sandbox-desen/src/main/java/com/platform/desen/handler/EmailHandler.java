package com.platform.desen.handler;

import cn.hutool.core.util.DesensitizedUtil;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * @Description 邮箱脱敏处理器
 * @Author kaiyang
 * @Date 2024-02-22 6:35 PM
 */

@Component
public class EmailHandler extends AbstractMaskHandler implements MaskHandler{

    // 邮箱匹配规则
    private static final Pattern PATTERN =
            Pattern.compile("([\\w]+(\\.[\\w]+)*@[\\w]+(\\.[\\w])+)");

    public int getStartIdx(String matcherGroupStr) {
        return 1;
    }

    public int getEndIdx(String matcherGroupStr) {

        return matcherGroupStr.length() - matcherGroupStr.indexOf("@");
    }

    public String regrex(String str) {
            return this.matcher(str, PATTERN);
    }
    // 关键字脱敏，使用 hutool 进行脱敏
    public String keyword(String str) {
        return DesensitizedUtil.email(str);
    }



}
