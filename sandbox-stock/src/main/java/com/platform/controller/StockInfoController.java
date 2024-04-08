package com.platform.controller;

import cn.hutool.core.collection.CollUtil;
//import com.platform.productserver.dto.FundDto;
//import com.platform.productserver.entity.EtfInfo;
//import com.platform.productserver.entity.StockInfo;
//import com.platform.productserver.service.StockService;
//import com.platform.productserver.stock.SnowStockUtils;
//import com.platform.productserver.stock.TianFundUtils;
import com.platform.dto.FundDto;
import com.platform.entity.EtfInfo;
import com.platform.entity.StockInfo;
import com.platform.service.StockService;
import com.platform.utils.SnowStockUtils;
import com.platform.utils.TianFundUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * stock 信息
 *
 * @Description stock 信息
 * @Date 2023-08-07 9:42 AM
 */
@Slf4j
@Controller
@RequestMapping(value = "api")
public class StockInfoController {

    @Autowired
    private StockService stockService;

    @Autowired
    private ExecutorService service;

    /**
     * ETF 信息列表
     * localhost:9501/api/etf/info/list
     * http://fund.eastmoney.com/data/fbsfundranking.html
     */
    @GetMapping(value = "etf/info/list")
    public String etfInfoList() {

        List<EtfInfo> etfInfos = TianFundUtils.etfInfoList();
        service.submit(() -> {
            stockService.saveEtfInfoList(etfInfos);
        });
        return "success";
    }

    /**
     * localhost:9501/api/stock/info/list
     */
    @GetMapping(value = "stock/info/list")
    public String stockInfoList() {


        for (int i = 0; i < 100; i++) {
            List<StockInfo> stockInfos = SnowStockUtils.queryStockList(i + 1, 100);
            log.info("page is {}",i );
            if (CollUtil.isNotEmpty(stockInfos)) {
                stockService.saveStockInfoList(stockInfos);
            }
        }
        return "success";
    }


    /**
     * localhost:9501/api/fund/info
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


        for (int i = 17; i < 100; i++) {
            try {
                log.info(" start page {}", i);
                List<FundDto> fundDtos = TianFundUtils.fundList(i + 1, "hh", "混合型");
                if (CollUtil.isEmpty(fundDtos)) {
                    break;
                }
                stockService.saveFundInfoList(fundDtos);
            } catch (Exception e) {
            }
        }

        return "success";

    }


}
