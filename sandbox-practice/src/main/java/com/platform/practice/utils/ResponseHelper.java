package com.platform.practice.utils;

import cn.hutool.http.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ResponseHelper {

    public static void response(HttpServletResponse response, String ret) {

        // response.setContentType("text/plain");
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void response(HttpServletResponse response, InputStream input) {

        OutputStream out = null;
        try {
            out = response.getOutputStream();
            int len = 0;
            byte[] b = new byte[1024];
            while ((len = input.read(b)) != -1) {
                out.write(b, 0, len);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String readData(HttpServletRequest request) {
        BufferedReader br = null;
        try {
            StringBuilder result = new StringBuilder();
            br = request.getReader();
            for (String line; (line = br.readLine()) != null; ) {
                if (result.length() > 0) {
                    result.append("\n");
                }
                result.append(line);
            }
            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void response(HttpServletResponse response, HttpResponse reset) {
        response.setStatus(reset.getStatus());
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        // 设置响应体
        byte[] body = reset.body() != null ? reset.body().getBytes(StandardCharsets.UTF_8) : new byte[0];
        try {
            response.getOutputStream().write(body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
