package com.platform.productserver.web;

import cn.hutool.core.collection.CollUtil;
import com.platform.productserver.dto.FundDto;
import com.platform.productserver.entity.EtfInfo;
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

    /**
     * localhost:9501/api/stock/fund/info
     *
     * @return
     */
    @GetMapping(value = "fund/info")
    public String fundInfoList() {

        // gp-股票型
        // hh-混合型
        // zq-债券型
        // zs-指数型
        // qdii-qdii
        // lof-lof
        // fof-fof

        List<EtfInfo> etfInfos = TianFundUtils.etfInfoList();

        stockService.saveEtfInfoList(etfInfos);

//        for (int i = 17; i < 100; i++) {
//            try {
//                log.info(" start page {}", i);
//                List<FundDto> fundDtos = TianFundUtils.fundList(i + 1, "hh", "混合型");
//                if (CollUtil.isEmpty(fundDtos)) {
//                    break;
//                }
//                stockService.saveFundInfoList(fundDtos);
//            } catch (Exception e) {
//            }
//        }

        return "success";

    }


}
