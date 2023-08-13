package com.platform.productserver.utils;

import java.math.BigDecimal;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 打印表格工具类
 */

@Slf4j
public class PrintTableUtils {

    // 分隔符
    private static final String SPT = "|";
    private static final String CNT = "-";
    private static final String ADD = "+";
    private static final String LIN = "\n";

    public static void main(String[] args) {

        OrderCalNode node = new OrderCalNode();
        node.setKeyId("345");
        node.setTotal(new BigDecimal("45"));
        node.setPoint(new BigDecimal("34"));
        node.setPointAct(new BigDecimal("2"));
        node.setCash(new BigDecimal("3"));
        node.setExpand(new BigDecimal("4"));
        node.setInflation(new BigDecimal("5"));

        List<OrderCalNode> nodeList = Lists.newArrayList();
        nodeList.add(node);
        nodeList.add(node);

        printResult(nodeList, OrderCalNode.class);


    }



    public static void printResult(List<OrderCalNode> calList, Class klass) {
        // 获取对象的信息
        Map<String, Field> fieldMap = ReflectUtil.getFieldMap(klass);
        Set<String> strings = fieldMap.keySet();
        // 去除字段 serialVersionUID
        strings.remove("serialVersionUID");
        // 数据的表头信息和数据行信息list
        List<String> fields = Lists.newArrayList(strings);
        List<List<String>> dataList = new ArrayList<>();
        for (OrderCalNode cal : calList) {
            // 获取每个字段的列表数据
            dataList.add(getObjValueList(cal, fields));
        }
        // 打印拆单结果
        printResult(fields, dataList);
    }


    // 获取数据行信息
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

    /**
     * 打印拆单结果
     */
    public static void printResult(List<String> title, List<List<String>> bodys) {

        // formats 存储每一列的数据长度,根据表头设置初始长度
        List<Integer> formats = new ArrayList<>(title.size());
        for (int i1 = 0; i1 < title.size(); i1++) {
            formats.add(i1, title.get(i1).length() + 2);
        }
        // 计算数据的列宽
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
        String spt = ADD;
        for (int k = 0; k < formats.size(); k++) {
            spt += StrUtil.repeat(CNT, formats.get(k)) + ADD;
        }
        // 数据信息 builder
        StringBuilder optResult = new StringBuilder();
        optResult.append(spt).append(LIN); // 添加行间分割符
        optResult.append(handleRow(title, formats)).append(LIN);
        optResult.append(spt).append(LIN); // 添加行间分割符
        for (int j = 0; j < bodys.size(); j++) {
            optResult.append(handleRow(bodys.get(j), formats)).append(LIN);
        }
        optResult.append(spt).append(LIN); // 添加行间分割符
        log.info("print split result \n{}", optResult.toString());

    }

    // 添加数据行信息
    private static String handleRow(List<String> data, List<Integer> widths) {
        List<String> res = handleData(data, widths);
        return SPT + CollUtil.join(res, SPT) + SPT;
    }

    // 处理数据 处理数据
    private static List<String> handleData(List<String> data, List<Integer> widths) {
        List<String> objects = Lists.newArrayList();
        int cnt = 0;
        for (String dat : data) {
            Integer width = widths.get(cnt++);
            objects.add(formatLength(dat, width));
        }
        return objects;
    }

    // 格式化展示数据
    private static String formatLength(Object val, Integer width) {
        // %-10s 左对齐 固定宽度 10
        // %10s  右对齐 固定宽度 10
        return String.format(StrUtil.format("%{}s", width), val.toString());
    }


}
