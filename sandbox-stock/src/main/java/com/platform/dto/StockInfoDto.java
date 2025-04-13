package com.platform.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class StockInfoDto {


    private List<String> codeList;

    private List<List<Object>> dataList;


    private JSONObject colors;


}
