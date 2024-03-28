package com.platform.migrate.web;


import com.alibaba.fastjson.JSONObject;
import com.platform.migrate.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping(value = "api")
@RestController
public class IndexController {


    @PostMapping(value = "index")
    public String index(@RequestBody UserDto dto) {
        log.info("information {} ", JSONObject.toJSONString(dto));
        return dto.toString();
    }


}
