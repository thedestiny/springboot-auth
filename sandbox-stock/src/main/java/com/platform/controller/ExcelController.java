package com.platform.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.github.javafaker.Faker;
import com.google.common.collect.Lists;
import com.platform.dto.AppExcelDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    @Value("${export.total.num:1000}")
    public Integer total;
    /**
     * 单次导出条数，默认10000
     */
    @Value("${export.perNum.size:100}")
    public Integer perNum;


    @PostMapping(value = "upload")
    public void upload(HttpServletRequest request, HttpServletResponse response) throws IOException {


//        EasyExcel.read("", AppExcelDto.class, new Ex)
//        EasyExcel.read(request.getInputStream(),  );


    }





    /**
     * 分批生成数据并导出 excel
     */
    @GetMapping(value = "download")
    public void download(HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里 URLEncoder.encode可以防止中文乱码 当然和EasyExcel没有关系
        String name = URLEncoder.encode("课程分类", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename="+ name + ".xlsx");


        ExcelWriter excelWriter = null;
        OutputStream outputStream = null;
        List<AppExcelDto> excelDtoList;
        int pages = total / perNum;
        int totalCount = 0;

        int line = 0;
        String fileName = "result" + DateUtil.format(new Date(), "YY-MM-DD");
        try {
            // 导出为一个 sheet
            // WriteSheet writeSheet = EasyExcelFactory.writerSheet("result").build();
            while (true) {
                // 导出为多个 sheet
                WriteSheet writeSheet = EasyExcelFactory.writerSheet("result-" + line).build();
                //分页查询
                excelDtoList = buildDataList();
                //根据数据导出excel
                if (CollUtil.isNotEmpty(excelDtoList)) {
                    if (outputStream == null) {
                        outputStream = getOutputStream(fileName, response);
                    }
                    if (excelWriter == null) {
                        excelWriter = EasyExcelFactory.write(outputStream, AppExcelDto.class)
                                .registerWriteHandler(excelStyle())
                                // .password("123456")
                                .build();
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

        //  SIMPLIFIED_CHINESE
        Faker faker = new Faker(new Locale("zh", "CN"));
        for (int i = 0; i < 100; i++) {

            AppExcelDto dto = new AppExcelDto();
            dto.setName(faker.name().fullName());
            dto.setAddress(faker.address().fullAddress());
            dataList.add(dto);
        }
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

    public static void main(String[] args) {

        Faker faker = new Faker(new Locale("zh", "CN"));

        System.out.println(faker.address().fullAddress());
        System.out.println(faker.name().fullName());

    }




}
