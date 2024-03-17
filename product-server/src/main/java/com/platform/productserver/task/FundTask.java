//package com.platform.productserver.task;
//
//import cn.hutool.core.collection.CollUtil;
//import com.platform.productserver.dto.FundDto;
//import com.platform.productserver.entity.EtfInfo;
//import com.platform.productserver.service.StockService;
//import com.platform.productserver.stock.TianFundUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * @Description fund 定时任务
// * @Date 2023-08-07 9:43 AM
// */
//
//@Slf4j
//@Component
//public class FundTask {
//
//
//    @Autowired
//    private StockService stockService;
//
//
//    /**
//     * 更新 ETF 交易信息
//     */
//    @Scheduled(cron = "20 59/1 * * * ?")
//    public void etfUpdateTask() {
//        log.info("update etf trade task !");
//
//        List<EtfInfo> etfInfos = stockService.queryEtfInfoList();
//        for (EtfInfo etfInfo : etfInfos) {
//            // 更新交易信息并保存
//            TianFundUtils.capEtfTradeInfo(etfInfo);
//            stockService.updateEtfInfo(etfInfo);
//        }
//    }
//
//    /**
//     * 更新 ETF 列表信息
//     */
//    @Scheduled(cron = "20 20 * * * ?")
//    public void etfInfo() {
//        List<EtfInfo> etfs = TianFundUtils.etfInfoList();
//        stockService.saveEtfInfoList(etfs);
//    }
//
//    /**
//     * 场外基金信息更新
//     * gp-股票型
//     * hh-混合型
//     * zq-债券型
//     * zs-指数型
//     * qdii-qdii
//     * lof-lof
//     * fof-fof
//     */
//    @Scheduled(cron = "20 1 * * * ?")
//    public void fundInfo() {
//        log.info("start fund task !");
//        Integer total = 0;
//        handleFundInfoList("hh", "混合型");
//        handleFundInfoList("gp", "股票型");
//        //
//        handleFundInfoList("zq", "债券型");
//        handleFundInfoList("zs", "指数型");
//
//        handleFundInfoList("qdii", "qdii");
//        handleFundInfoList("lof", "lof");
//        handleFundInfoList("fof", "fof");
//    }
//
//    // 存储基金信息
//    private void handleFundInfoList(String typ, String tp) {
//
//        for (int i = 0; i < 100; i++) {
//            try {
//                log.info("{} start page {}", tp, i);
//                List<FundDto> funds = TianFundUtils.fundList(i + 1, typ, tp);
//                if (CollUtil.isNotEmpty(funds)) {
//                    stockService.saveFundInfoList(funds);
//                } else {
//                    break;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//}
