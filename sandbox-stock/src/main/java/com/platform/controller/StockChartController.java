package com.platform.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.platform.dto.StockInfoDto;
import com.platform.dto.StockLineDto;
import com.platform.utils.StockLineUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Controller
public class StockChartController {


    /**
     * 颜色
     */
    private static final String colors = "#00008b\n" +
            "#f00\n" +
            "#ffde00\n" +
            "#002a8f\n" +
            "#003580\n" +
            "#ed2939\n" +
            "#000000\n" +
            "#003897\n" +
            "#f93\n" +
            "#bc002d\n" +
            "#024fa2\n" +
            "#000000\n" +
            "#00247d\n" +
            "#ef2b2d\n" +
            "#dc143c\n" +
            "#d52b1e\n" +
            "#e30a17\n" +
            "#00247d\n" +
            "#b22234";

    @GetMapping(value = "stock")
    public String index() {
        log.info("data ");
        return "index";
    }

    @GetMapping(value = "test01")
    public String test01() {
        log.info("data ");
        return "test01";
    }

    @GetMapping(value = "test02")
    public String test02() {
        log.info("data ");
        return "test02";
    }


    @GetMapping(value = "/data/asset/data/life-expectancy-table", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String lifeExpectancy() {
        String property = System.getProperty("user.dir") + File.separator + "sandbox-stock\\src\\main\\resources\\data\\";
        String dat = FileUtil.readString(property + "life-expectancy-table.json", "UTF-8");
        return dat;
    }

    @GetMapping(value = "/data", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String data() {
        String property = System.getProperty("user.dir") + File.separator + "sandbox-stock\\src\\main\\resources\\data\\";
        String dat = FileUtil.readString(property + "data.json", "UTF-8");
        return dat;
    }

    @GetMapping(value = "/data/list")
    @ResponseBody
    public String stockDataList() {
        return "dat";
    }


    /**
     *  数据k线图
     */
    @GetMapping(value = "/api/stock/data/list", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String dataLine(String start, String end, String klt, String codes) {

        StockInfoDto infoDto = new StockInfoDto();
        List<StockLineDto> dtos = new ArrayList<>();
        String[] split = codes.split(",");
        for (String code : split) {
            dtos.addAll(StockLineUtils.queryKlineData(code, start, end, klt));
        }

        List<String> codeList = dtos.stream().map(StockLineDto::getName)
                .distinct().collect(Collectors.toList());

        dtos.sort(Comparator.comparing(StockLineDto::getDate)
                .thenComparing(StockLineDto::getCode));
        List<List<Object>> lists = StockLineUtils.queryKlineList(dtos);

        String[] split1 = colors.split("\n");
        JSONObject nodeList = new JSONObject();
        for (String s : codeList) {
            nodeList.put(s, split1[RandomUtil.randomInt(0, split1.length - 1)]);
        }

        infoDto.setDataList(lists);
        infoDto.setCodeList(codeList);
        infoDto.setColors(nodeList);
        String resp = JSONObject.toJSONString(infoDto);

        return resp;
    }



}
