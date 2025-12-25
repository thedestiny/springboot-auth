package com.platform.authcommon.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-12-25 2:19 PM
 */

@Slf4j
public class RepoUtils {

    public static void main(String[] args) {

        File[] ls = FileUtil.ls("/Users/admin/Desktop/project_tmp");
        for (File file : ls) {
            handleProject(file);
        }


    }

    private static void handleProject(File file) {
        String name = file.getName();
        for (File ele : FileUtil.ls(file.getPath())) {
            if (ele.isDirectory() && !StrUtil.contains(ele.getPath(), ".") && !StrUtil.contains(ele.getPath(), "sql")) {

                List<String> allFile = handPro(ele);
                Set<String> pathSet = handleFile(allFile);
                if (CollUtil.isNotEmpty(pathSet)) {
                    String out = "";
                    if (ele.getName().equals("src")) {
                        // System.out.println(name);
                        out += name + "\t" + name;
                    } else {
                        // System.out.println(name + "\t" + ele.getName());
                        out += name + "\t" + ele.getName();
                    }
                    Set<String> tmp = new HashSet<>();
                    for (String s : pathSet) {
                        String packageName = s.split("/java/")[1].replace("/", ".");
                        // System.out.println(packageName);
                        // tmp.add(packageName);
                        tmp.add(simpleName(packageName));
                    }
                    out += "\t" + CollUtil.join(tmp, ",");
                    System.out.println(out);
                }
            }

        }
    }

    private static String simpleName(String path) {
        return path.split("controller")[0] + "controller";
        // System.out.println(path);
    }

    private static Set<String> handleFile(List<String> allFile) {
        Set<String> pathSet = new HashSet<>();
        if (CollUtil.isEmpty(allFile)) {
            return pathSet;
        }

        for (String s : allFile) {
            if (s.contains("web") || s.contains("controller")) {
                File file = new File(s);
                String path = file.getParent();
                // System.out.println(path);
                pathSet.add(path);
            }
        }

        return pathSet;

    }

    private static List<String> handPro(File fil) {

        List<String> result = new ArrayList<>();
        if (fil.isDirectory()) {
            File[] ls = FileUtil.ls(fil.getPath());
            for (File l : ls) {
                List<String> strings = handPro(l);
                result.addAll(strings);
            }
        } else {
            if (fil.getName().endsWith(".java") && !fil.getPath().contains("test")) {
                result.add(fil.getPath());
            }
        }
        return result;

    }
}
