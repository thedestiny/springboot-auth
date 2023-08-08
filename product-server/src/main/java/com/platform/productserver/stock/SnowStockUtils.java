package com.platform.productserver.stock;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.platform.productserver.entity.StockInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SnowStockUtils {

    // 获取stock 列表
    public final static String tmp = "https://stock.xueqiu.com/v5/stock/screener/quote/list.json?page={}&size={}&order=desc&orderby=code&order_by=symbol&market=CN&type=sh_sz";
    // cookie
    public final static String cookie = "device_id=2b38bc16558c7136729b7ca56964df25; s=af11m6tu48; xq_a_token=715ae77c7b72c67549b80e153e894ef2e19f0446; xq_r_token=a1c71f74d5f0fd50f87640a0682c837e5a07f706; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTY5MjkyMzcyMSwiY3RtIjoxNjkxMjk4NDEwMzk2LCJjaWQiOiJkOWQwbjRBWnVwIn0.aIP_BZmsXrJ3UuEzXUETjHjS4F5tl5PO1z9zLF_z6YgOt8HCa6g9MufuI2IPsZuCxaEQMM2JJnHQJnEb5RjGlGlez5uX_pVSMugf_GcvKbDWetO7t9qQ6bc5E3d6kQxAqp22a9mXYbtOm483XJiwIWrxUDgLfL_9JCOoeLwC6nulDWDTsqURbiJOB1vqfKj4I91A-q16ylmq6w_pAQxHXNz2weCAMBD-gYdpiSH_GjoMwfyNA7LrcU98dcrL_pdKPgXwzpzJ7Uy-l8olN-HeByTFXsT5Aa_d1tkambcK3B3nnDREzZs1RIEYzwEhiLqgFiKuHn3owsyEAOwrBLaiJQ; Hm_lvt_1db88642e346389874251b5a1eded6e3=1691258397,1691298436; u=391691298436287; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1691298483";

    /**
     * 获取股票列表信息
     */
    public static List<StockInfo> queryStockList(Integer page, Integer limit) {
        List<StockInfo> resultList = new ArrayList<>();
        String format = StrUtil.format(tmp, page, limit);
        HttpRequest request = HttpUtil.createGet(format);
        request.header("Cookie", cookie);
        HttpResponse execute = request.execute();
        JSONObject json = JSONObject.parseObject(execute.body());
        JSONObject data = json.getJSONObject("data");
        JSONArray list = data.getJSONArray("list");
        if(list == null){
            return resultList;
        }
        for (int i = 0; i < list.size(); i++) {
            JSONObject node = list.getJSONObject(i);
            String symbol = node.getString("symbol"); // 股票代码
            Integer code = Integer.valueOf(StrUtil.sub(symbol, 2, symbol.length()));
            StockInfo stock = new StockInfo();
            stock.setId(symbol);
            stock.setName(node.getString("name")); // 股票名称
            stock.setPercent(node.getBigDecimal("percent")); // 当天涨跌幅
            stock.setAmplitude(node.getBigDecimal("amplitude")); // 振幅,百分比
            stock.setCurrent(node.getBigDecimal("current"));  // 当前价格
            stock.setCurrentYearPercent(node.getBigDecimal("current_year_percent"));  // 当年的涨幅
            stock.setTurnoverRate(node.getBigDecimal("turnover_rate"));   // 换手率
            stock.setDividendYield(node.getBigDecimal("dividend_yield"));   // 股息率
            BigDecimal capital = NumberUtil.div(node.getBigDecimal("market_capital").toBigInteger(), 100_000_000);
            stock.setMarketCapital(capital.setScale(3, BigDecimal.ROUND_HALF_UP));  // 市值
            stock.setPb(node.getBigDecimal("pb"));  // 市净率
            stock.setEps(node.getBigDecimal("eps"));   // 每股收益
            stock.setChg(node.getBigDecimal("chg")); // 涨跌(元)
            stock.setPeTtm(node.getBigDecimal("pe_ttm"));  // 静态市盈率
            stock.setPbTtm(node.getBigDecimal("pb_ttm"));  // 市净率(ttm)
            stock.setNetValue(node.getBigDecimal("net_value"));  // 每股净资产
            resultList.add(stock);
        }
        return resultList;
    }

    public static void main(String[] args) {

        JSONObject jsonObject = queryStockLine("SZ002032");
        StockInfo info = new StockInfo();
        // info.setId("SZ301388");
        info.setId("SH600519");
        // SZ301388
        // JSONObject infs = queryStockInfo(info);

        // List<StockInfo> stocks = queryStockList(1, 90);
//         for (int i = 0; i < stocks.size(); i++) {
//             StockInfo stock = stocks.get(i);
//             JSONObject infos = queryStockInfo(stock);
//             String high = infos.getString("52周最高");
//             String low = infos.getString("52周最低");
//             String pe = infos.getString("市盈率(动)");
//             String yield = infos.getString("股息率(TTM)");
//             System.out.println(stock.getId() + "\t" + stock.getName() + "\t" + stock.getCurrent() + "\t" + pe + "\t" + yield);
//         }

    }


    private static Integer handleFocus(String text, String val) {
        val = StrUtil.trim(val);
        if (text.contains("万")) {
            BigDecimal dg = new BigDecimal(val);
            BigDecimal mul = NumberUtil.mul(dg, 10000);
            return mul.intValue();
        } else {
            return Integer.valueOf(val);
        }
    }


    /**
     * 根据代码查询股票信息
     */
    public static JSONObject queryStockInfo(StockInfo stock) {
        // 获取股票详情页数据
        String url = "https://xueqiu.com/S/" + stock.getId();
        HttpRequest request = HttpUtil.createGet(url);
        HttpResponse execute = request.execute();
        Document parse = Jsoup.parse(execute.body()); // 使用 jsoup 解析 html
        Element element = parse.getElementsByClass("quote-container").get(0); // 获取 节点信息
        Element focusTime = element.getElementsByClass("stock-time").get(0); // 球友关注和数据时间
        // 1755 球友关注 休市 08-04 15:34:51 北京时间
        List<String> split = StrUtil.split(focusTime.text(), " ");
        // System.out.println(split);
        String focus = split.get(0);
        DateTime parse1 = DateUtil.parse(DateUtil.date().year() + "-" + split.get(3), "yyyy-MM-dd");
        stock.setUpdateTime(parse1);
        stock.setFocus(handleFocus(focusTime.text(), focus));
        // System.out.println(focus);
        // System.out.println(parse1);
        Element table = element.getElementsByClass("quote-info").get(0);  // 表格
        Elements tds = table.getElementsByTag("td");
        JSONObject result = new JSONObject();
        for (int i = 0; i < tds.size(); i++) {
            Element ele = tds.get(i);
            // 替换中文符号为英文
            String text = ele.text().replace("：", ":")
                    .replace("（", "(").replace("）", ")");
            if (!text.contains(":")) {
                continue;
            }
            String[] arrs = text.split(":");
            result.put(arrs[0].trim(), arrs[1].trim());
        }
        // for (Map.Entry<String, Object> entry : result.entrySet()) {
        //     String key = entry.getKey();
        //     Object value = entry.getValue();
        //     System.out.println(key + "  ==  " + value);
        // }
        return result;


    }

    public static final String kline = "https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol={}&begin=1691505314056&period=week&type=before&count=-284&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance";

    /**
     * 查询股票 k 线数据
     * @param code
     * @return
     */
    public static JSONObject queryStockLine(String code){

        String format = StrUtil.format(kline, code);
        HttpRequest request = HttpUtil.createGet(format);
        request.header("Cookie", cookie);
        HttpResponse execute = request.execute();
        String body = execute.body();
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONObject data = jsonObject.getJSONObject("data");
        // 数据以及对应的列名称
        JSONArray item = data.getJSONArray("item");
        JSONArray column = data.getJSONArray("column");
        System.out.println(jsonObject);


        return null;



    }
}
