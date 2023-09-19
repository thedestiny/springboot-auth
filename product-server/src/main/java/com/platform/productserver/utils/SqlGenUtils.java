package com.platform.productserver.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Date 2023-09-18 10:33 AM
 */
public class SqlGenUtils {


    public static void main(String[] args) {

        String colums = "  id, account_id, merchant_no, acc_no, account_type, request_no, order_no, other_account,\n" +
                "other_account_type, action_type, prod_type, trans_amount, balance, remark, `source`, app_id, create_time";

         batchSave(colums);
        // resultMap(colums);


    }

    public static void resultMap(String colums) {

        String[] split = colums.split(",");

        String pid = "";
        String pidU = "";
        List<String> ids = Lists.newArrayList();
        for (String s : split) {
            String trim = s.trim();
            if (StrUtil.isBlank(trim)) {
                continue;
            }

            String s1 = StrUtil.toCamelCase(trim);
            if (StrUtil.isBlank(pid)) {
                pid = trim;
                pidU = s1;
                String nd = "   <id column=\"" + pid + "\" property=\"" + pidU + "\"/>";
                ids.add(nd);

            } else {
                String nd = "   <result column=\"" + trim + "\" property=\"" + s1 + "\"/>";
                ids.add(nd);
            }
        }
        String tmp = "<resultMap id=\"BaseResultMap\" type=\"com.platform.productserver.entity.CtransLog\">\n";
        String tmp1 = "\n</resultMap>";

        String element = tmp + CollUtil.join(ids, "\n") + tmp1;

        System.out.println(element);

    }

    public static void update(String colums) {

        String[] split = colums.split(",");

        List<String> col1 = new ArrayList<>();

        String node = " <if test=\"{} != null\">\n" +
                "     {} = #{NODE},\n" +
                " </if>";

        String pid = "";
        String pidU = "";
        for (String s : split) {
            String trim = s.trim();
            if (StrUtil.isBlank(trim)) {
                continue;
            }

            String s1 = StrUtil.toCamelCase(trim);
            if (StrUtil.isBlank(pid)) {
                pid = trim;
                pidU = s1;
            }
            String node1 = StrUtil.format(node, s1, trim).replace("NODE", s1);
            col1.add(node1);

        }

        String tmp = "update tab_name \n<set>\n";
        tmp += CollUtil.join(col1, "\n");
        tmp += "\n</set>\n";
        tmp += "where " + pid + " = #{" + pidU + "}";
        System.out.println(tmp);


    }

    public static void batchSave(String colums) {


        String[] split = colums.split(",");

        String tmp = "insert into tab_name ( {}) \nvalues ";
        String tmp1 = "\n<foreach collection=\"entities\" item=\"entity\" separator=\",\">";
        String tmp2 = " </foreach>";

        List<String> col1 = new ArrayList<>();
        List<String> col2 = new ArrayList<>();
        List<String> col3 = new ArrayList<>();
        for (String s : split) {
            String trim = s.trim();
            if (StrUtil.isBlank(trim)) {
                continue;
            }
            col1.add(" " + trim);
            String s1 = StrUtil.toCamelCase(trim);
            col2.add(" #{entity." + s1 + "}");
            col3.add(" #{" + s1 + "}");
        }

        String join1 = CollUtil.join(col1, ",").trim();
        String join2 = "\n(" + CollUtil.join(col2, ",").trim() + ")\n";
        String join3 = "\n(" + CollUtil.join(col3, ",").trim() + ")\n";

//        System.out.println(join1);
//        System.out.println(join2);

        String format = StrUtil.format(tmp, join1) + tmp1 + join2 + tmp2;
        System.out.println(format);

        System.out.println("\n\n\n");

        System.out.println(StrUtil.format(tmp, join1) + join3);
    }

}
