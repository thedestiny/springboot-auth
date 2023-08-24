package com.platform.productserver.stock;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import com.google.common.collect.Maps;
import com.platform.productserver.dto.LineDto;
import com.platform.productserver.utils.PrintTableUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Description
 * @Date 2023-08-23 4:31 PM
 */
public class StockCmpUtils {


    public static void main(String[] args) {

        // https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH000001&begin=1692866399564&period=week&type=before&count=-284&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance
        // https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH510050&begin=1692866498053&period=month&type=before&count=-284&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance

        // SH510050 day
        List<LineDto> dtoList = SnowStockUtils.queryStockLine("SH510050", "day");
        PrintTableUtils.printResultTab(dtoList, LineDto.class);


        HashMap<String, String> map = Maps.newHashMap();

        map.put("SH510050", "上证50");

        // ReflectUtil.



    }

    public static void stockCompare(){



    }






}
