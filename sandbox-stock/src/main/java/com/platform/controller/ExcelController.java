package com.platform.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.google.common.collect.Lists;
import com.platform.dto.AppExcelDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-04-12 10:05 AM
 */

@Slf4j
@Controller
@RequestMapping(value = "excel")
public class ExcelController {


    /**
     * 导出上限。默认100000
     */
    @Value("${total.export.ceiling:100000}")
    public Integer total;
    /**
     * 单次导出条数，默认10000
     */
    @Value("${perNum.export.size:100000}")
    public Integer perNum;


    @GetMapping(value = "download")
    public void download(HttpServletRequest request, HttpServletResponse response){

        ExcelWriter excelWriter = null;
        OutputStream outputStream = null;
        List<AppExcelDto> excelDtoList;
        int pages = total / perNum;
        int totalCount = 0;

        int line = 0;
        String fileName = "result" + DateUtil.format(new Date(), "YY-MM-DD");
        try {
            WriteSheet writeSheet = EasyExcelFactory.writerSheet(fileName).build();
            while (true) {
                //分页查询

                excelDtoList = buildDataList();

                //根据数据导出excel
                if (CollUtil.isNotEmpty(excelDtoList)) {
                    if (outputStream == null) {
                        outputStream = getOutputStream(fileName, response);
                    }
                    if (excelWriter == null) {
                        excelWriter = EasyExcelFactory.write(outputStream, AppExcelDto.class)
                                .registerWriteHandler(excelStyle()).build();
                    }
                    excelWriter.write(excelDtoList, writeSheet);
                    totalCount = totalCount + excelDtoList.size();
                }
                line += 1;
                if (line >= pages || excelDtoList.size() < perNum) {
                    break;
                }
            }
            if (totalCount > 0) {
                //发送Kafka消息
            }
        } catch (Exception e) {
            log.error(" export excel error error list:{}", e);
        } finally {
            closeOutputStream(excelWriter, outputStream);
        }


    }

    protected HorizontalCellStyleStrategy excelStyle(){
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 字体策略
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short) 10);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        //头策略使用默认 设置字体大小
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 10);
        headWriteCellStyle.setWriteFont(headWriteFont);
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }

    public void closeOutputStream(ExcelWriter excelWriter, OutputStream outputStream){
        if(excelWriter != null){
            excelWriter.finish();
        }
        if(outputStream != null){
            try {
                outputStream.close();
            } catch (IOException e) {
                log.error("export excel error error list:{}",e);
            }
        }
    }

    private List<AppExcelDto> buildDataList() {

        List<AppExcelDto> dataList = Lists.newArrayList();





        return dataList;
    }


    public OutputStream getOutputStream(String fileName, HttpServletResponse response) throws IOException {
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Pragma", "private");
        response.setHeader("Cache-Control", "private");
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Type", "application/force-download");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        return response.getOutputStream();
    }




}
