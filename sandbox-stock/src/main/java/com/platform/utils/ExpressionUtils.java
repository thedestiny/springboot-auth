package com.platform.utils;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-03-21 10:50 AM
 */

@Slf4j
public class ExpressionUtils {


    public static void main(String[] args) {


        String expression = "1 + 3 + 5 + 4 / 2  ";
        // 直接执行表达式 不需要多次执行相同的表达式
        Long execute1 = (Long) AviatorEvaluator.execute(expression);
        log.info("result is {}", execute1);

        // 编译成可重复执行的表达式对象，以提高性能, 需要重复执行
        Expression exp = AviatorEvaluator.compile(expression);
        Object execute = exp.execute();
        log.info("result is {} ", execute);

        Expression lexp = AviatorEvaluator.compile("a>b? a+b:a*b");
        Map<String, Object> env = new HashMap<>();
        env.put("a", 2);
        env.put("b", 1);
        Long execute2 = (Long) lexp.execute(env);
        log.info("result2 is {} ", execute2);

//        AviatorEvaluator.getInstance().setOption(Options.ALWAYS_PARSE_FLOATING_POINT_NUMBER_INTO_DECIMAL, true);
//        AviatorEvaluator.setOption(Options.ALWAYS_PARSE_FLOATING_POINT_NUMBER_INTO_DECIMAL, true);
        Map<String, Object> env1 = new HashMap<>();
        env1.put("a", BigDecimal.valueOf(2));
        env1.put("b", BigDecimal.valueOf(6));
        BigDecimal execute3 = (BigDecimal) AviatorEvaluator.execute("a /b ", env1);
        log.info("execute3 is {} ", execute3);


    }


}
