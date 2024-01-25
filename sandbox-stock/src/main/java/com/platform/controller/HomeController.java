package com.platform.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.platform.dto.FundDto;
import com.platform.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-01-25 10:40 AM
 */

@Slf4j
@RestController
@RequestMapping(value = "api")
public class HomeController {


    @GetMapping(value = "home")
    public Result<FundDto> task() {

        FundDto dto = new FundDto();
        dto.setCode("123456");
        dto.setName("中文");
        dto.setUpdateDate(DateUtil.today());
        dto.setWeek(new BigDecimal("10"));
        dto.setCompany("江淮汽车");

        Result<FundDto> success = Result.success(dto);
        log.info("data is {}", JSONObject.toJSONString(dto));
        return success;
    }

    @GetMapping(value = "home1")
    public String home1() {

        FundDto dto = new FundDto();
        dto.setCode("123456");
        dto.setName("中文");
        dto.setUpdateDate(DateUtil.today());
        dto.setWeek(new BigDecimal("10"));
        dto.setCompany("江淮汽车");

        HashMap<String,String> maps = new HashMap<>();
        maps.put("3","4" );

        log.info("data is {}", JSONObject.toJSONString(dto));
        return JSONObject.toJSONString(dto);
    }


}
