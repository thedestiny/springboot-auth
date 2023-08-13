package com.platform.orderserver.utils;

import com.platform.authcommon.utils.IdGenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;

/**
 * @Description response 信息响应
 * @Author
 * @Date 2023-07-19 10:32 AM
 */
@Slf4j
public class ResponseUtils {

    private static final String chartset = "UTF-8";

    /**
     * 写 excel  xlsx
     *
     * @param response
     */
    public static void writeExcel(HttpServletResponse response, XSSFWorkbook workbook) {
        OutputStream out = null;
        try {
            String filename = "导出" + IdGenUtils.id() + ".xlsx";
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename,chartset ));
            // response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setContentType("application/octet-stream;charset=UTF-8");
            out = response.getOutputStream();
            workbook.write(out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void response(HttpServletResponse response, String body) {
        base(response, body, "text/html;charset=UTF-8");
    }

    public static void responseJson(HttpServletResponse response, String body) {

        base(response, body, "application/json;charset=UTF-8");
    }

    private static void base(HttpServletResponse response, String body, String header) {

        try {

            PrintWriter writer = response.getWriter();
            response.setContentType(header);
            writer.write(body);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            log.error("error is {} detail ", e.getMessage(), e);
        }
    }
}
