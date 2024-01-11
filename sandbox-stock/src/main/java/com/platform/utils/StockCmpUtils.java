package com.platform.utils;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.platform.dto.LineDto;
import com.platform.dto.StockCompDto;
import com.platform.entity.StockInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Date 2023-08-23 4:31 PM
 */
public class StockCmpUtils {


    public static void main(String[] args) {

        // https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH000001&begin=1692866399564&period=week&type=before&count=-284&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance
        // https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH510050&begin=1692866498053&period=month&type=before&count=-284&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance

        // SH510050 day
        // List<LineDto> dtoList = SnowStockUtils.queryStockLine("SH510050", "day");
        // PrintTableUtils.printResultTab(dtoList, LineDto.class);


        Map<String, String> map = Maps.newHashMap();

        map.put("SH510050", "上证50");
        map.put("SH512880", "证券ETF");
        map.put("SZ159870", "化工ETF");
        map.put("SH510760", "上证指数");
        map.put("SH510210", "上证ETF");
        map.put("SZ159903", "深成ETF");
        map.put("SZ510300", "300ETF");
        map.put("SZ588050", "科创ETF");
        // map.put("SZ510300", "创业板50ETF");
        // map.put("SZ510300", "500ETF");

        List<StockCompDto> dtos = Lists.newArrayList();

        StockInfo info = new StockInfo();
        info.setPe(BigDecimal.ONE);
        info.setId("SH510000");
        info.setName("ETF");
        info.setAmount(BigDecimal.valueOf(200));
        List<StockInfo> infs = Lists.newArrayList(info, info, info, info);

        dtos.add(new StockCompDto("SH512880", "证券ETF", infs));
        dtos.add(new StockCompDto("SZ159870", "化工ETF", infs));
        dtos.add(new StockCompDto("SH510210", "上证ETF"));
        dtos.add(new StockCompDto("SZ159903", "深成ETF"));
        dtos.add(new StockCompDto("SH588050", "科创ETF", infs));
        dtos.add(new StockCompDto("SH510760", "上证ETF"));
        dtos.add(new StockCompDto("SH510300", "300ETF", infs));
        dtos.add(new StockCompDto("SH510050", "上证50"));

        for (StockCompDto dto : dtos) {
            stockCompare(dto);
        }
        PrintTableUtils.printResultTab(dtos, StockCompDto.class);


    }

    public static void stockCompare(StockCompDto dto) {

        try {
            List<LineDto> dtoList = SnowStockUtils.queryStockLine(dto.getCode(), "day");
            if (CollUtil.isNotEmpty(dtoList)) {
                LineDto lineDto = dtoList.get(0);
                dto.setPrice(lineDto.getClose());
                dto.setRate(lineDto.getPercent());
            }
        } catch (Exception e) {

        }


    }


}
