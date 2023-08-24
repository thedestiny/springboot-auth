package com.platform.productserver.stock;

import com.platform.productserver.dto.LineDto;
import com.platform.productserver.utils.PrintTableUtils;

import java.util.List;

/**
 * @Description
 * @Date 2023-08-23 4:31 PM
 */
public class StockCmpUtils {


    public static void main(String[] args) {

        // https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH000001&begin=1692866399564&period=week&type=before&count=-284&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance

        // https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH510050&begin=1692866498053&period=month&type=before&count=-284&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance

        List<LineDto> dtoList = SnowStockUtils.queryStockLine("SH.510050", 1691684478473L, "week");
        PrintTableUtils.printResultTab(dtoList, LineDto.class);


    }


}
