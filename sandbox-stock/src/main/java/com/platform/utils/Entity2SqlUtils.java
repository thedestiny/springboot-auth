package com.platform.utils;

import cn.hutool.core.util.StrUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Entity2SqlUtils {


    private static Pattern linePattern = Pattern.compile("_(\\w)");

    /**
     * 下划线转驼峰
     */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 驼峰转下划线
     */
    public static String humpToLine(String str) {
        str = str + "";
        String s = str.replaceAll("[A-Z0-9]", "_$0").toLowerCase();
        return s.startsWith("_") ? StrUtil.sub(s, 1,s.length()) : s;
    }

    public static String transType(String type) {
        if ("Integer".equals(type)) {
            return "int";
        }
        if ("Long".equals(type)) {
            return "bigint";
        }
        if ("BigDecimal".equals(type) || "Double".equals(type) || "Float".equals(type)) {
            return "decimal(18,2)";
        }
        if ("Date".equals(type) || "LocalDateTime".equals(type)) {
            return "datetime";
        }
        if ("String".equals(type)) {
            return "varchar(255)";
        }
        if ("Boolean".equals(type)) {
            return "char(20)";
        }
        return "no";
    }

    public static void main(String[] args) {

        String entity = "AddressBook";

        String columns = " ";

        String result = StrUtil.format("CREATE TABLE `{}` (\n", humpToLine(entity));


        for (String node : columns.split("\n")) {
            node = StrUtil.blankToDefault(node, "").trim();
            if (node.startsWith("private")) {
                String tmp = node.replace(" ", "-");
                String replace = tmp.replace("--", "-").split(";")[0];
                String[] split = replace.split("-");
                //  split[2];//
                String field =  humpToLine(split[2]);
                String type = transType(split[1]);
                String tp = StrUtil.format("  `{}` {}  NULL COMMENT '{}',\n", field, type, split[2]);
                if(field.equals("id")){
                    tp = StrUtil.format("  `{}` {} NOT NULL AUTO_INCREMENT COMMENT '{}',\n", field, type, split[2]);
                }

                result += tp;
            }
        }
        result += "  PRIMARY KEY (`id`) USING BTREE\n";
        result += ") ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COMMENT='" + entity+"';\n";

        System.out.println("\n" + result);

    }


}
