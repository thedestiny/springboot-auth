package com.platform.productserver.utils;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.nacos.common.utils.MD5Utils;
import com.google.common.collect.Lists;
import com.platform.productserver.order.OrderInfoDto;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description
 * @Date 2023-09-07 8:33 PM
 */
public class Java8Utils {


    public static void main(String[] args) {

        String dd = "E10ADC3949BA59ABBE56E057F20F883E";
        System.out.println(MD5Utils.md5Hex("123456", "UTF-8"));

        // 实践数据
        OrderInfoDto dto1 = new OrderInfoDto("123", BigDecimal.valueOf(5), "456");
        OrderInfoDto dto2 = new OrderInfoDto("234", BigDecimal.valueOf(4), "456");
        OrderInfoDto dto3 = new OrderInfoDto("123", BigDecimal.valueOf(3), "234");
        OrderInfoDto dto4 = new OrderInfoDto("345", BigDecimal.valueOf(2), "345");

        List<OrderInfoDto> dtoList = Lists.newArrayList(dto1, dto2, dto3, dto4);

        // 根据订单号去重
        List<OrderInfoDto> filterList = dtoList.stream().
                collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(OrderInfoDto::getOrderNo))), ArrayList::new));

        // 根据 订单号做映射
        Map<String, OrderInfoDto> collect1 = dtoList.parallelStream()
                .collect(Collectors.toMap(OrderInfoDto::getOrderNo, Function.identity(), (k1, k2) -> k2, HashMap::new));
        // 根据订单号做分组
        Map<String, List<OrderInfoDto>> collect2 = dtoList.parallelStream()
                .collect(Collectors.groupingBy(OrderInfoDto::getOrderNo));

        // 金额累计计算和
        BigDecimal reduce1 = dtoList.stream().filter(node -> NumberUtil.isGreater(node.getAmount(), BigDecimal.ONE))
                .map(OrderInfoDto::getAmount).reduce(BigDecimal.ZERO, NumberUtil::add);
        System.out.println("reduce1 " + reduce1);

        BigDecimal reduce2 = dtoList.stream().filter(node -> NumberUtil.isGreater(node.getAmount(), BigDecimal.ONE))
                .collect(Collectors.reducing(BigDecimal.ZERO, OrderInfoDto::getAmount, NumberUtil::add));
        System.out.println("reduce2 " + reduce2);
        // 根据用户id 分组进行累计求和
        Map<String, BigDecimal> collect = dtoList.stream().filter(node -> NumberUtil.isGreater(node.getAmount(), BigDecimal.ONE))
                .collect(Collectors.groupingBy(OrderInfoDto::getUserId, Collectors.reducing(BigDecimal.ZERO, OrderInfoDto::getAmount, NumberUtil::add)));

        System.out.println("collect " + collect);
        // compute
        Map<String, BigDecimal> result = new HashMap<>();
        Map<String, BigDecimal> orders = new HashMap<>();
        Map<String, BigDecimal> results = new HashMap<>();
        Map<String, BigDecimal> merges = new HashMap<>();
        results.put("123", BigDecimal.valueOf(1));

        for (OrderInfoDto dto : dtoList) {
            String userId = dto.getUserId();
            String orderNo = dto.getOrderNo();
            BigDecimal amount = dto.getAmount();
            // 根据用户id分组对金额求和, compute 计算
            result.compute(userId, (key, value) -> NumberUtil.add(value, amount));
            // 如果不存在则进行计算
            orders.computeIfAbsent(orderNo, key -> amount);
            // 如果存在则进行计算
            results.computeIfPresent(userId, (key, value) -> NumberUtil.add(value, amount));
            // 进行数据合并
            merges.merge(userId, amount, (val1, val2) -> NumberUtil.add(val1, val2));
        }

        merges.putIfAbsent("123", BigDecimal.valueOf(9));
        // 包含 key 且等于旧值才替换
        merges.replace("234", BigDecimal.valueOf(34), BigDecimal.valueOf(2));
        // 包含 key 则替换
        merges.replace("456", BigDecimal.valueOf(34));

        BigDecimal orDefault = result.getOrDefault("123", BigDecimal.ZERO);

        System.out.println("result " + result);
        System.out.println("orders " + orders);
        System.out.println("results " + results);
        System.out.println("merges " + merges);


    }
}
