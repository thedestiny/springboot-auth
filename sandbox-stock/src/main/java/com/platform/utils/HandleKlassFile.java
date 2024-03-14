package com.platform.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;

public class HandleKlassFile {

    public static void main(String[] args) {

        String path = "pro_path\\aries";

        String dd = "/* 292 */";
        System.out.println(dd.length());
        System.out.println("/*     */".length());

        handleFile(path);



    }

    public static void handleFile(String path){
        File[] files = FileUtil.ls(path);
        // 分割下标
        Integer lineIdx = 9;

        for (File file : files) {
            System.out.println("file is " + file.getName() + "  path" + file.getPath());
            if (file.isDirectory()){
                 handleFile(file.getPath());
            }
            if(file.isFile() && file.getName().endsWith(".java")){

                String cont = FileUtil.readString(file, "UTF-8");
                String[] split = cont.split("\n");
                StringBuilder builder = new StringBuilder();
                for (String line : split) {
                    if(line.startsWith("/* Location: ")){
                        break;
                    }
                    String sub = StrUtil.sub(line, 0, lineIdx).trim();
                    if(sub.endsWith("*/")){
                        builder.append(StrUtil.sub(line, lineIdx, line.length())).append("\n");
                    } else {
                        builder.append(line).append("\n");
                    }
                }
                // 写文件内容
                FileUtil.writeString(builder.toString(),file.getPath(),"UTF-8");

            }



        }
    }

}
