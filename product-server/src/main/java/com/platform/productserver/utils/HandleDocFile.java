package com.platform.productserver.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;

/**
 * 处理项目文件
 * @Description
 * @Date 2023-12-25 9:26 上午
 */
public class HandleDocFile {


    public static void main(String[] args) {


        File[] files = FileUtil.ls("/pro_path");

        for (File file : files) {
            handleFile(file);
        }


    }

    public static void handleFile(File file) {

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                handleFile(file1);
            }
        }

        if (file.isFile() && file.getName().endsWith(".java")) {
            String path = file.getPath();
            List<String> cntList = FileUtil.readLines(path, "UTF-8");
            List<String> list = Lists.newArrayList();
            System.out.println(path);
            for (String node : cntList) {
                if (node.contains("/*") && node.contains("*/")) {

                    String s = StrUtil.subAfter(node, "*/", false);
                    // System.out.println(StrUtil.sub(s, 1, s.length()));
                    list.add(StrUtil.sub(s, 1, s.length()));
                }
                // System.out.println(node);
                if (node.contains("/* Location:") || node.contains(" * Java compiler version:") || node.contains(" * JD-Core Version:") || node.contains(" */")) {
                    continue;
                }
                list.add(node);
            }

            String join = CollUtil.join(list, "\n");
            FileUtil.writeString(join,path, "UTF-8");
           //  System.out.println(join);


        }


    }


}
