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

    // 订单总金额, shouldAmount
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
     * 根据 keyId 获取订单拆单结果
     *
     * @param keyId
     * @return
     */
    public OrderCalNode queryByKeyId(String keyId) {
        return calResult.get(keyId);
    }

    /**
     * 开始计算
     */
    public void calculate() {

        if (CollUtil.isEmpty(calList)) {
            return;
        }

        // 按照金额倒序排列
        // CollUtil.sort(calList, Comparator.comparing(OrderCalNode::getTotal).reversed());
        // 膨胀金 立减 珑珠实付 累计之和
        BigDecimal expandAcc = BigDecimal.ZERO;
        BigDecimal inflationAcc = BigDecimal.ZERO;
        BigDecimal pointActAcc = BigDecimal.ZERO;

        // len 行 * 6 列 二维数组
        for (int i = 0; i < calList.size(); i++) {
            OrderCalNode node = calList.get(i);
            // 子订单总金额
            BigDecimal sub = node.getTotal();
            if (i == calList.size() - 1) {
                // 最后一行使用减法
                // 计算膨胀金
                node.setExpand(NumberUtil.sub(expand, expandAcc));
                // 计算立减
                node.setInflation(NumberUtil.sub(inflation, inflationAcc));
                // 计算珑珠实付
                node.setPointAct(NumberUtil.sub(pointAct, pointActAcc));
            } else {
                // 计算膨胀金
                node.setExpand(handleIfNull(sub, total, node.getExpand(), expand));
                // 计算立减
                node.setInflation(handleIfNull(sub, total, node.getInflation(), inflation));
                // 计算珑珠实付
                node.setPointAct(handleIfNull(sub, total, node.getPointAct(), pointAct));

                // 计算累计分配金额
                expandAcc = NumberUtil.add(expandAcc, node.getExpand());
                inflationAcc = NumberUtil.add(inflationAcc, node.getInflation());
                pointActAcc = NumberUtil.add(pointActAcc, node.getPointAct());
            }
            // 计算现金金额, 现金计算使用减法
            node.calculateCashPoint();
        }
        // 累计加和 现金金额
        BigDecimal reduce = calList.stream().map(OrderCalNode::getCash).reduce(BigDecimal.ZERO, NumberUtil::add);
        if (!NumberUtil.equals(reduce, cashAmt)) {
            throw new RuntimeException("拆单异常!");
        }
        // 暂存计算结果
        this.calResult = calList.stream().collect(Collectors.toMap(OrderCalNode::getKeyId, Function.identity()));


        Map<String, Field> fieldMap = ReflectUtil.getFieldMap(OrderCalNode.class);
        Set<String> strings = fieldMap.keySet();
        strings.remove("serialVersionUID");
        List<String> fields = Lists.newArrayList(strings);
        List<List<String>> dataList = new ArrayList<>();
        for (OrderCalNode cal : calList) {
            dataList.add(getObjValueList(cal, fields));
        }

        // 打印拆单结果
        printResult(fields, dataList);


    }

    // result = (sub / deno) * cal
    public BigDecimal handleIfNull(BigDecimal sub, BigDecimal deno, BigDecimal val, BigDecimal cal) {
        if (val != null) {
            return val;
        }
        BigDecimal div = NumberUtil.div(sub, deno, 4);
        return NumberUtil.mul(div, cal).setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 打印拆单结果
     */
    public void printResult(List<String> title, List<List<String>> bodys ) {


//        List<String> title = Lists.newArrayList("keyId", "total", "lz", "lzAct", "cash", "expand", "inflation");
//        List<List<String>> bodys = Lists.newArrayList();
//
//
//        builder.append(CollUtil.join(handleData(title), "\t")).append("\n");
//        int i = 0;
//        for (Map.Entry<String, OrderCalNode> data : calResult.entrySet()) {
//            OrderCalNode value = data.getValue();
//            List<String> res = Lists.newArrayList(value.getKeyId(), value.getTotal().toPlainString(),
//                    value.getPoint().toPlainString(), value.getPointAct().toPlainString(), value.getCash().toPlainString(),
//                    value.getExpand().toPlainString(), value.getInflation().toPlainString());
//            bodys.add(res);
//            builder.append(CollUtil.join(handleData(res), "\t")).append("\n");
//        }
        StringBuilder builder = new StringBuilder();
        // 需要设置初始长度
        List<Integer> formats = new ArrayList<>(title.size());
        for (int i1 = 0; i1 < title.size(); i1++) {
            formats.add(i1, title.get(i1).length() + 2);
        }

        // 计算列宽
        for (int i1 = 0; i1 < bodys.size(); i1++) {
            List<String> body = bodys.get(i1);
            for (int t = 0; t < body.size(); t++) {
                Integer dat = formats.get(t);
                if (dat < body.get(t).length() + 2) {
                    formats.set(t, body.get(t).length() + 2);
                }

            }
        }
        // 数据分割符号
        String spt = "+";
        for (int i1 = 0; i1 < formats.size(); i1++) {
            spt += StrUtil.repeat("-", formats.get(i1)) + "+";
        }

        StringBuilder optResult = new StringBuilder();
        optResult.append(spt).append("\n");
        optResult.append(handleRow(title, formats)).append("\n");
        optResult.append(spt).append("\n");
        for (int i1 = 0; i1 < bodys.size(); i1++) {
            optResult.append(handleRow(bodys.get(i1), formats)).append("\n");
        }
        optResult.append(spt).append("\n");
        log.info("print split result \n{}", builder.toString());
        log.info("print split result \n{}", optResult.toString());

    }

    // 分隔符
    private static final String SPT = "|";

    private String handleRow(List<String> data, List<Integer> widths) {
        List<String> res = handleData(data, widths);
        String rt = SPT + CollUtil.join(res, SPT) + SPT;
        System.out.println(rt);
        return rt;
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

        Map<String, OrderCalNode> calResult = splitCal.getCalResult();
        for (Map.Entry<String, OrderCalNode> data : calResult.entrySet()) {
            System.out.println(data.getValue());
        }


        OrderCalNode cal = new OrderCalNode();
        cal.setKeyId("233");
        cal.setTotal(new BigDecimal("2"));
        cal.setPoint(new BigDecimal("2"));
        cal.setPointAct(new BigDecimal("3"));
        cal.setCash(new BigDecimal("4"));
        cal.setExpand(new BigDecimal("5"));
        cal.setInflation(new BigDecimal("6"));


    }

    private static List<String> getObjValueList(OrderCalNode cal, List<String> fields) {


        List<String> dats = new ArrayList<>();
        for (String field : fields) {
            Object val = ReflectUtil.getFieldValue(cal, field);
            String dat = "";
            if (val != null) {
                if (val instanceof Date) {
                    dat = DateUtil.format((Date) val, "yyyy-MM-dd HH:mm:ss");
                } else {
                    dat = val.toString();
                }
            }
            dats.add(dat);
        }
        return dats;
    }

    private String formatLength(Object val) {
        return formatLength(val, 10);
    }

    public int dataLength(Object val) {
        String dat = "";
        if (val != null) {
            if (val instanceof Date) {
                dat = DateUtil.format((Date) val, "yyyy-MM-dd HH:mm:ss");
            } else {
                dat = val.toString();
            }
        }
        return dat.length();
    }


    private String formatLength(Object val, Integer width) {
        // %-10s 左对齐 固定宽度 10
        // %10s  右对齐 固定宽度 10
        // %10s  右对齐 固定宽度 10
        return String.format(StrUtil.format("%{}s", width), val.toString());
    }


    /**
     * 处理数据
     */
    private List<String> handleData(List<String> data, List<Integer> widths) {
        List<String> objects = Lists.newArrayList();
        int cnt = 0;
        for (String dat : data) {
            Integer width = widths.get(cnt++);
            objects.add(formatLength(dat, width));
        }
        return objects;
    }
}
