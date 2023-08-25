package com.platform.productserver.web;

import cn.hutool.core.collection.CollUtil;
import com.platform.productserver.dto.FundDto;
import com.platform.productserver.service.StockService;
import com.platform.productserver.stock.TianFundUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Description
 * @Date 2023-08-07 9:42 AM
 */
@Slf4j
@Controller
@RequestMapping(value = "api/stock")
public class StockInfoController {

    @Autowired
    private StockService stockService;

    @GetMapping(value = "fund/info")
    public String fundInfoList() {


        for (int i = 0; i < 100; i++) {
            try {
                log.info(" start page {}", i);
                List<FundDto> fundDtos = TianFundUtils.fundList(i + 1, "hh", "混合型");
                if (CollUtil.isEmpty(fundDtos)) {
                    break;
                }
                stockService.saveFundInfoList(fundDtos);
            }catch (Exception e){

            }

        }

        return "success";

    }


}
