package com.platform.flex.web;

import com.alibaba.fastjson.JSONObject;
import com.platform.flex.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description
 * @Date 2023-11-14 2:11 PM
 */


@Slf4j
@RestController
@RequestMapping(value = "api")
public class IndexController {

    @Autowired
    private StudentService studentService;

    @DeleteMapping(value = "/batch/{ids}")
    public String index(@PathVariable Long[] ids) {





        log.info("ids is {}", JSONObject.toJSONString(ids));
        return "success";
    }


}
