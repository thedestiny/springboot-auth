package com.platform.orderserver.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析java 项目文档
 * @Description
 * @Author kaiyang
 * @Date 2023-07-20 4:12 PM
 */

@Slf4j
public class BossTest {

    final static String t1 = "project_path";


    public static void main(String[] args) {


        List<String> fileList = new ArrayList<>();
        fileList(new File(t1), fileList);


    }


    /**
     * 查找所有需要查找的接口类
     */
    private static List<String> fileList(File file, List<String> fileList) {

        if (file.isDirectory()) {
            File[] ls = FileUtil.ls(file.toString());
            for (int k = 0; k < ls.length; k++) {
                File fl = ls[k];
                fileList(fl, fileList);
            }
        }

        if (file.isFile() && file.toString().contains("main") && file.toString().contains("java") && !file.toString().contains("test")) {
            try {
                JavaProjectBuilder builder = new JavaProjectBuilder();
                //源码目录
                builder.addSourceTree(file);
                JavaClass javaClass = ((List<JavaClass>) builder.getClasses()).get(0);

                List<JavaAnnotation> annotations = javaClass.getAnnotations();
                String s1 = klazzPrefix(annotations);
                String pkg1 = javaClass.getPackage().getName();
                String addr = pkg1 + "." + file.getName();
                // log.info("file {}", );
                // 文件名
                String name = javaClass.getName();
                String pkg = javaClass.getSource().getPackage().getName();
                List<String> imports = javaClass.getSource().getImports();

                List<JavaMethod> methods = javaClass.getMethods();
                for (JavaMethod method : methods) {
                    String name1 = method.getName();
                    String codeBlock = method.getCodeBlock();
                    boolean b = hasMapping(method.getAnnotations());
                    if (!b) {
                        continue;
                    }
                    // log.info("code {}", codeBlock);
                    String s = methodUrl(method.getAnnotations());
                    boolean b1 = hasFunction(method.getAnnotations());
                    if (!b1) {
                        String comment = apiSwagger(method.getAnnotations());
                        System.out.println(addr + "\t" + name1 + "\t" + s1 + s + "\t" + comment);
                        // System.out.println("url is \n" + s1 + s);
                    }
                }
            } catch (Exception e) {
                // log.error("file is {}", file);
            }

        }
        return fileList;
    }

    private static boolean hasMapping(List<JavaAnnotation> annotations) {

        for (JavaAnnotation annotation : annotations) {
            String name = annotation.getType().getName();
            if (name.contains("Mapping")) {
                return true;
            }
        }
        return false;
    }


    private static String apiSwagger(List<JavaAnnotation> annotations) {

        for (JavaAnnotation annotation : annotations) {
            String name = annotation.getType().getName();
            if (name.contains("ApiOperation")) {
                String value = annotation.getNamedParameter("value").toString();
                String replace = value.replace("\"", "");
                // System.out.println(replace);
                return replace;
            }
        }
        return "";
    }

    private static boolean hasFunction(List<JavaAnnotation> annotations) {

        for (JavaAnnotation annotation : annotations) {
            String name = annotation.getType().getName();
            if (name.contains("Authority")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 类上的 RequestMapping 前缀
     *
     * @param annotations
     * @return
     */
    public static String klazzPrefix(List<JavaAnnotation> annotations) {
        String res = "";
        for (JavaAnnotation annotation : annotations) {
            String name = annotation.getType().getName();
            if (!StrUtil.equals(name, "RequestMapping")) {
                continue;
            }
            String value = annotation.getNamedParameterMap().get("value").toString();
            res = value.replaceAll("\"", "");
            if (!res.startsWith("/")) {
                res = "/" + res;
            }
            // System.out.println(res);
        }
        return res;
    }

    /**
     * 方法上的地址
     *
     * @param annotations
     * @return
     */
    public static String methodUrl(List<JavaAnnotation> annotations) {
        String res = "";
        for (JavaAnnotation annotation : annotations) {
            String name = annotation.getType().getName();
            if (!StrUtil.contains(name, "Mapping")) {
                continue;
            }
            String value = annotation.getNamedParameterMap().get("value").toString();
            res = value.replaceAll("\"", "").replaceAll("]", "").replaceAll("\\[", "");
            if (!res.startsWith("/")) {
                res = "/" + res;
            }
            // System.out.println(res);
        }
        return res;
    }
}
