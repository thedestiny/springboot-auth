package com.platform.productserver.task;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.platform.productserver.entity.StockInfo;
import com.platform.productserver.mapper.StockInfoMapper;
import com.platform.productserver.stock.SnowStockUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description stock 定时任务
 * @Date 2023-08-07 9:43 AM
 */

@Slf4j
@Component
public class StockTask {

    @Autowired
    private StockInfoMapper stockInfoMapper;

    /**
     * 沪深股市一览表，每页90条，一共 57页数据
     * https://xueqiu.com/hq#exchange=CN&firstName=1&secondName=1_0
     */
    @Scheduled(cron = "20 1/2 * * * ?")
    public void task() {
        log.info("start task !");
        Integer total = 0;
        for (int i = 0; i < 57; i++) {
            List<StockInfo> stockInfos = SnowStockUtils.queryStockList(i + 1, 90);
            if (CollUtil.isEmpty(stockInfos)) {
                return;
            }
            total += stockInfos.size();
            log.info("stock page {} size {}", i + 1, total);
            for (StockInfo node : stockInfos) {
                try {
                    JSONObject infos = SnowStockUtils.queryStockInfo(node);
                    String high = infos.getString("52周最高");
                    String low = infos.getString("52周最低");
                    String yield = infos.getString("股息率(TTM)");
                    node.setHighYear(new BigDecimal(high));
                    node.setLowYear(new BigDecimal(low));
                    // log.info("stock code {} and name {}", node.getId(), node.getName());
                    stockInfoMapper.saveStockInfo(node);
                } catch (Exception e) {
                    log.error("code is error {}", e.getMessage(), e);
                }

            }
        }
    }


}
