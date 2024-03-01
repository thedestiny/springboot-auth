package com.platform.controller;


import com.alibaba.fastjson.JSONObject;
import com.platform.config.AppDataConfig;
import com.platform.config.Person;
import com.platform.dto.Result;
import com.platform.dto.UserDto;
import com.platform.task.StockTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "api")
public class IndexController {


    @Autowired
    private StockTask task;

    @Autowired
    private Person person;

    @Autowired
    private AppDataConfig dataConfig;


    @GetMapping(value = "index")
    public Result<UserDto> task(){

        // task.task();
        log.info("person data {}", JSONObject.toJSONString(person));
        log.info("config data {}", dataConfig);
        UserDto dto = new UserDto();
        dto.setUsername("梁开阳");
        dto.setCellphone("12348599321");
        dto.setAddress("河南省郑州市管城区23号");
        dto.setEmail("xieyue86@163.com");
        dto.setAge(10);
        log.info("user info {}", dto);
        log.info("user info {}", JSONObject.toJSONString(dto));

        return Result.success(dto);
    }

}
