package com.platform.productserver.utils;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Data
public class OrderSplitCalUtils {

    // 订单总金额
    private BigDecimal total;
    // 积分金额
    private BigDecimal point;
    // 实付积分
    private BigDecimal pointAct;
    // 膨胀金额
    private BigDecimal expand;
    // 立减金额
    private BigDecimal inflation;
    // 现金金额
    private BigDecimal cashAmt;

    private List<OrderCalNode> calList = new ArrayList<>();
    private Map<String, OrderCalNode> calResult;

    // 初始化计算对象
    public static OrderSplitCalUtils init(BigDecimal total, BigDecimal lzAmt, BigDecimal lzActAmt, BigDecimal expand, BigDecimal inflation, BigDecimal cashAmt) {

        if (!NumberUtil.sub(total, lzAmt, cashAmt).equals(BigDecimal.ZERO)) {
            throw new RuntimeException("数据初始化异常");
        }
        if (!NumberUtil.sub(lzAmt, lzActAmt, expand, inflation).equals(BigDecimal.ZERO)) {
            throw new RuntimeException("优惠信息有误!");
        }
        OrderSplitCalUtils cal = new OrderSplitCalUtils();
        cal.setTotal(total);
        cal.setPoint(lzAmt);
        cal.setPointAct(lzActAmt);
        cal.setExpand(expand);
        cal.setInflation(inflation);
        cal.setCashAmt(cashAmt);
        return cal;
    }

    /**
     * 根据 keyId 获取订单拆单结果
     */
    public OrderCalNode queryByKeyId(String keyId) {
        return calResult.get(keyId);
    }

    /**
     * 添加订单子单数据
     */
    public void addRow(String keyId, BigDecimal total, BigDecimal expand, BigDecimal inflation) {
        OrderCalNode orderCalNode = new OrderCalNode();
        orderCalNode.setKeyId(keyId);
        orderCalNode.setTotal(total);
        orderCalNode.setExpand(expand);
        orderCalNode.setInflation(inflation);
        calList.add(orderCalNode);
    }

    public void addRow(String keyId, BigDecimal total, BigDecimal expand) {
        addRow(keyId, total, expand, null);
    }

    public void addRow(String keyId, BigDecimal total) {
        addRow(keyId, total, null);
    }


    /**
     * 开始计算
     */
    public void calculate() {
        if (CollUtil.isEmpty(calList)) {
            return;
        }
        CollUtil.sort(calList, Comparator.comparing(OrderCalNode::getTotal).reversed()); // 按照金额倒序排列
        // 膨胀金 立减 积分实付 累计之和
        BigDecimal expandAcc = BigDecimal.ZERO;
        BigDecimal inflationAcc = BigDecimal.ZERO;
        BigDecimal pointActAcc = BigDecimal.ZERO;
        // len 行 * 6 列 二维数组
        for (int i = 0; i < calList.size(); i++) {
            OrderCalNode node = calList.get(i);
            BigDecimal sub = node.getTotal(); // 子订单总金额
            if (i < calList.size() - 1) {
                // 每单的订单明细可分配金额 和 实际分配金额
                BigDecimal residue, actual;
                BigDecimal calExpand = handleIfNull(sub, total, node.getExpand(), expand);
                // 每单剩余可分配金额表示取计算值
                residue = node.getExpand() != null ? node.getExpand() : calNodeRemainAmount(node);
                // 横向约束值 计算值 纵向约束值
                actual = NumberUtil.min(residue, calExpand, NumberUtil.sub(expand, expandAcc));
                node.setExpand(actual); // 计算膨胀金
                node.setInflation(handleIfNull(sub, total, node.getInflation(), inflation)); // 计算立减
                node.setPointAct(handleIfNull(sub, total, node.getPointAct(), pointAct)); // 计算积分实付
                // 计算累计分配金额
                expandAcc = NumberUtil.add(expandAcc, node.getExpand());
                inflationAcc = NumberUtil.add(inflationAcc, node.getInflation());
                pointActAcc = NumberUtil.add(pointActAcc, node.getPointAct());

            } else {

                // 最后一行使用减法
                node.setExpand(NumberUtil.sub(expand, expandAcc)); // 计算膨胀金
                node.setInflation(NumberUtil.sub(inflation, inflationAcc)); // 计算立减
                node.setPointAct(NumberUtil.sub(pointAct, pointActAcc)); // 计算积分实付
            }
            node.calculateCashPoint(); // 计算现金金额, 现金计算使用减法
        }
        // 累计加和 现金金额，并且进行校验
        BigDecimal reduce = calList.stream().map(OrderCalNode::getCash).reduce(BigDecimal.ZERO, NumberUtil::add);
        if (!NumberUtil.equals(reduce, cashAmt)) {
            throw new RuntimeException("拆单异常!");
        }
        // 暂存计算结果
        this.calResult = calList.stream().collect(Collectors.toMap(OrderCalNode::getKeyId, Function.identity()));

        PrintTableUtils.printResult(calList, OrderCalNode.class);
    }

    /**
     * 计算每个子单的剩余可分配金额
     *
     * @param node
     * @return BigDecimal
     */
    private BigDecimal calNodeRemainAmount(OrderCalNode node) {
        return NumberUtil.sub(node.getTotal(), node.getExpand(), node.getInflation(), node.getPointAct(), node.getCash());
    }


    // result = (sub / deno) * cal
    public BigDecimal handleIfNull(BigDecimal sub, BigDecimal deno, BigDecimal val, BigDecimal cal) {
        if (val != null) {
            return val;
        }
        BigDecimal div = NumberUtil.div(sub, deno, 4);
        return NumberUtil.mul(div, cal).setScale(0, BigDecimal.ROUND_HALF_UP);
    }


    public static void main(String[] args) {

        // 初始化拆单对象
        OrderSplitCalUtils splitCal = OrderSplitCalUtils.init(BigDecimal.valueOf(101), BigDecimal.valueOf(100),
                BigDecimal.valueOf(97), BigDecimal.valueOf(2), BigDecimal.valueOf(1), BigDecimal.valueOf(1));
        // 添加计算数据
        splitCal.addRow("456", BigDecimal.valueOf(1));
        splitCal.addRow("123", BigDecimal.valueOf(100));

        // 开始拆单计算
        splitCal.calculate();
        // 获取订单拆单结果
        OrderCalNode orderCalNode = splitCal.queryByKeyId("123");

        Map<String, OrderCalNode> calResult = splitCal.getCalResult();
        for (Map.Entry<String, OrderCalNode> data : calResult.entrySet()) {
            System.out.println(data.getValue());
        }


    }


}
