package com.platform.productserver.utils;

import cn.hutool.core.util.StrUtil;

import java.util.HashMap;
import java.util.Map;

public class TablePrint {

    public static void main(String[] args) {


        String[] heads = {"序号", "姓名", "年龄", "地址"};
        String[][] body = {
                {"123", "小明", "23", "中国北京"},
                {"456", "小李", "12", "河南郑州"},
        };

        printTable(heads, body);


    }

    //按表格打印数据
    public static void printTable(String[] heads, String[][] data) {
        if (null == heads || heads.length <= 0) {
            throw new RuntimeException("标头heads[]不能为空!");
        }

        if (null == data || null == data[0] || data[0].length <= 0) {
            throw new RuntimeException("数据data[][]不能为空!");
        }
        if (heads.length != data[0].length) {
            throw new RuntimeException("标头与数据列数不一致!");
        }
        //数据列数
        int rowNum = heads.length;
        //数据行数
        int lineNum = data.length;
        //每列数据宽度
        Map<Integer, Integer> rowWidthMap = getRowWidthMap(rowNum, lineNum, heads, data);

        //打印表头
        printHead(rowNum, rowWidthMap, heads);
        //打印数据
        printData(rowNum, lineNum, rowWidthMap, data);

    }

    //获取列宽度-每列取最大
    public static Map<Integer, Integer> getRowWidthMap(int rowNum, int lineNum, String[] heads, String[][] data) {
        Map<Integer, Integer> rowWidthMap = new HashMap<>();
        //先记入表头各列宽度
        for (int i = 0; i < rowNum; i++) {
            int len = length(heads[i]);
            if (null == rowWidthMap.get(i)) {
                rowWidthMap.put(i, len);
            } else if (rowWidthMap.get(i) < len) {
                rowWidthMap.put(i, len);
            }
        }
        //比较并记入数据的最大宽度
        for (int i = 0; i < lineNum; i++) {
            for (int j = 0; j < rowNum; j++) {
                int len = length(data[i][j]);
                //System.out.println("[" + i + "," + j + "]:" + data[i][j] + ",width：" + len);
                if (null == rowWidthMap.get(j)) {
                    rowWidthMap.put(j, len);
                } else if (rowWidthMap.get(j) < len) {
                    rowWidthMap.put(j, len);
                }
            }
        }
        return rowWidthMap;
    }

    //打印表格中的横线
    public static void printLine(int rowNum, Map<Integer, Integer> map) {
        for (int i = 0; i < rowNum; i++) {
            int len = map.get(i);
            System.out.print("+");
            for (int k = 0; k < len; k++) {
                System.out.print("-");
            }
        }
        System.out.print("+");
        System.out.println();
    }

    public static int length(String str) {

        if (StrUtil.isBlank(str)) {
            return 0;
        }
        return str.length();
    }

    //打印表头内容
    public static void printHead(int rowNum, Map<Integer, Integer> rowWidthMap, String[] heads) {
        printLine(rowNum, rowWidthMap);
        for (int h = 0; h < rowNum; h++) {
            System.out.print("|");
            int actLength = rowWidthMap.get(h);
            int dataLength = length(heads[h]);
            executePrintData(actLength, dataLength, heads[h]);
            if (h == rowNum - 1) {
                System.out.print("|");
            }
        }
        System.out.println();
        printLine(rowNum, rowWidthMap);
    }

    //打印表数据
    public static void printData(int rowNum, int lineNum, Map<Integer, Integer> rowWidthMap, String[][] data) {
        for (int j = 0; j < lineNum; j++) {
            for (int i = 0; i < rowNum; i++) {
                System.out.print("|");
                int dataLength = length(data[j][i]);
                int actLength = rowWidthMap.get(i);
                executePrintData(actLength, dataLength, data[j][i]);
                if (i == rowNum - 1) {
                    System.out.print("|");
                }
            }

            System.out.println();
            printLine(rowNum, rowWidthMap);
        }
        System.out.println();
    }

    //执行打印数据
    public static void executePrintData(int actLength, int dataLength, String data) {
        if (actLength > dataLength) {
            int num = actLength - dataLength;
            if (num == 1) {
                System.out.print(data);
                System.out.print(" ");
            } else {
                int beforeNum = num / 2;
                int afterNum = num - beforeNum;
                for (int m = 0; m < beforeNum; m++) {
                    System.out.print(" ");
                }
                System.out.print(data);
                for (int m = 0; m < afterNum; m++) {
                    System.out.print(" ");
                }
            }
        } else {
            System.out.print(data);
        }
    }


}
