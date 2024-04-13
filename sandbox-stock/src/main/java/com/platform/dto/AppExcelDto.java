package com.platform.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-04-12 10:08 AM
 */

@ContentRowHeight(20)// 表体行高
@HeadRowHeight(20)// 表头行高
@ColumnWidth(14)// 列宽
@Data
@NoArgsConstructor
public class AppExcelDto implements Serializable {

    private static final long serialVersionUID = -5524846652762595935L;
    @ExcelProperty(value = "姓名", index = 0)
    private String name;
    @ExcelProperty(value = "地址", index = 1)
    private String address;



}
