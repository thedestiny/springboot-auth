package com.platform.controller;


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


    @GetMapping(value = "index")
    public String task(){

        task.task();

        return "success";
    }

}
