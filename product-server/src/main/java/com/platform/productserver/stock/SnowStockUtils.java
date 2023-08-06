package com.platform.productserver.stock;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.platform.productserver.entity.Stock;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SnowStockUtils {

    // 获取stock 列表
    public final static String tmp = "https://stock.xueqiu.com/v5/stock/screener/quote/list.json?page={}&size={}&order=desc&orderby=code&order_by=symbol&market=CN&type=sh_sz";
    // cookie
    public final static String cookie = "device_id=2b38bc16558c7136729b7ca56964df25; s=af11m6tu48; xq_a_token=715ae77c7b72c67549b80e153e894ef2e19f0446; xq_r_token=a1c71f74d5f0fd50f87640a0682c837e5a07f706; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTY5MjkyMzcyMSwiY3RtIjoxNjkxMjk4NDEwMzk2LCJjaWQiOiJkOWQwbjRBWnVwIn0.aIP_BZmsXrJ3UuEzXUETjHjS4F5tl5PO1z9zLF_z6YgOt8HCa6g9MufuI2IPsZuCxaEQMM2JJnHQJnEb5RjGlGlez5uX_pVSMugf_GcvKbDWetO7t9qQ6bc5E3d6kQxAqp22a9mXYbtOm483XJiwIWrxUDgLfL_9JCOoeLwC6nulDWDTsqURbiJOB1vqfKj4I91A-q16ylmq6w_pAQxHXNz2weCAMBD-gYdpiSH_GjoMwfyNA7LrcU98dcrL_pdKPgXwzpzJ7Uy-l8olN-HeByTFXsT5Aa_d1tkambcK3B3nnDREzZs1RIEYzwEhiLqgFiKuHn3owsyEAOwrBLaiJQ; Hm_lvt_1db88642e346389874251b5a1eded6e3=1691258397,1691298436; u=391691298436287; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1691298483";

    /**
     * 获取股票列表信息
     */
    public static List<Stock> queryStockList(Integer page, Integer limit) {
        List<Stock> resultList = new ArrayList<>();
        String format = StrUtil.format(tmp, page, limit);
        HttpRequest request = HttpUtil.createGet(format);
        request.header("Cookie", cookie);
        HttpResponse execute = request.execute();
        JSONObject json = JSONObject.parseObject(execute.body());
        JSONObject data = json.getJSONObject("data");
        JSONArray list = data.getJSONArray("list");
        for (int i = 0; i < list.size(); i++) {
            JSONObject node = list.getJSONObject(i);
            String symbol = node.getString("symbol"); // 股票代码
            Stock stock = new Stock();
            stock.setId(symbol);
            stock.setName(node.getString("name")); // 股票名称
            stock.setCurrent(node.getBigDecimal("current"));  // 当前价格
            Integer code = Integer.valueOf(StrUtil.sub(symbol, 2, symbol.length()));
            BigDecimal yearPercent = node.getBigDecimal("current_year_percent");  // 当年的涨幅
            BigDecimal turnoverRate = node.getBigDecimal("turnover_rate");   // 换手率
            BigDecimal dividendYield = node.getBigDecimal("dividend_yield");    // 股息率
            BigDecimal marketCapital = node.getBigDecimal("market_capital");  // 市值
            BigDecimal peTtm = node.getBigDecimal("pe_ttm");    // 静态市盈率
            BigDecimal pb = node.getBigDecimal("pb");    // 市净率
            BigDecimal eps = node.getBigDecimal("eps");   // 每股收益
            resultList.add(stock);
        }
        return resultList;
    }

    public static void main(String[] args) {

        // SZ301388
        // JSONObject infos = queryStockInfo("SZ301388");
        List<Stock> stocks = queryStockList(1, 90);
        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            JSONObject infos = queryStockInfo(stock.getId());
            String high = infos.getString("52周最高");
            String low = infos.getString("52周最低");
            String pe = infos.getString("市盈率(动)");
            String yield = infos.getString("股息率(TTM)");
            System.out.println(stock.getId() + "\t" + stock.getName() + "\t" + stock.getCurrent() + "\t" + pe + "\t" + yield);
        }

    }


    /**
     * 根据代码查询股票信息
     */
    public static JSONObject queryStockInfo(String symbol) {
        // 获取股票详情页数据
        String url = "https://xueqiu.com/S/" + symbol;
        HttpRequest request = HttpUtil.createGet(url);
        HttpResponse execute = request.execute();
        Document parse = Jsoup.parse(execute.body()); // 使用 jsoup 解析 html
        Element element = parse.getElementsByClass("quote-container").get(0); // 获取 节点信息
        Element focusTime = element.getElementsByClass("stock-time").get(0); // 球友关注和数据时间
        // 1755 球友关注 休市 08-04 15:34:51 北京时间
        List<String> split = StrUtil.split(focusTime.text(), " ");
        String focus = split.get(0);
        DateTime parse1 = DateUtil.parse(split.get(3), DateUtil.date().year() + "MM-dd");
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
}
