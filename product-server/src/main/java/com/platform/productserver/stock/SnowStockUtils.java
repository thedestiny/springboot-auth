package com.platform.productserver.stock;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.platform.productserver.dto.LineDto;
import com.platform.productserver.entity.StockInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class SnowStockUtils {

    // 获取stock 列表
    public final static String tmp = "https://stock.xueqiu.com/v5/stock/screener/quote/list.json?page={}&size={}&order=desc&orderby=code&order_by=symbol&market=CN&type=sh_sz";
    // cookie
    public final static String cookie = "device_id=2b38bc16558c7136729b7ca56964df25; s=af11m6tu48; xq_a_token=5ebe9dee46079c18ce86d1b568ceb4f4a6f40ed6; xqat=5ebe9dee46079c18ce86d1b568ceb4f4a6f40ed6; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOjgxNTg3ODA5NDYsImlzcyI6InVjIiwiZXhwIjoxNjk0MDEwNjc0LCJjdG0iOjE2OTE0MTg2NzQzMDQsImNpZCI6ImQ5ZDBuNEFadXAifQ.iAU87KeSQnhNLNzZbiTH5w-X20PYvqdm2w19PUF7cDBcMCp91-HN2W4izwqbNCxhCpUJF5lrjNTDlgOfORpvwCSFU8eUQXeGrvONa2k-YZFYNMZfFDkE66JWYfXbBj7nZMp4WuDVB3ByCIUuDSBXM_bkaIuA2Tbxib2MoHpaqhnMIMSzsYg6jxgoLOLbQOLYP0YOZ2ud_xOg5ufSqTD4BzQg016mGNS6r55DPUYVQX_WF5iJQyTCwZnaHWblwF5QNpYxE9EvZ_cb3sGRM1dF9R14B9ZzBmY7g5rbfXtj9Jw7AwkAIKmEMfJgu06zfLMviWbU5rw01y_nePHltbv8Pw; xq_r_token=7c7bb53ce65f0eb34bbf61a2de4e68778f33f960; xq_is_login=1; u=8158780946; bid=db8e4f1e50c43c68f8fff28eae72dd3f_ll3vxqz5; Hm_lvt_1db88642e346389874251b5a1eded6e3=1691298436,1691414145,1691594916,1691673376; is_overseas=0; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1691673420";

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
        if (list == null) {
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

        List<LineDto> dtoList = queryStockLine("SZ002032", 1691684478473L, "week");
        StockInfo info = new StockInfo();
        // info.setId("SZ301388");
        info.setId("SH600519");

        List<StockInfo> stocks = queryStockList(1, 900);
        for (StockInfo stock : stocks) {
            try {
                List<LineDto> dtoList1 = queryStockLine(stock.getId(), 1691684478473L, "week");
                stockModel(stock, dtoList1);
            } catch (Exception e){}



        }


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

    /**
     * k 线数据获取接口列表
     */
    public static final String kline = "https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol={}&begin={}&period={}&type=before&count=-284&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance";

    /**
     * 查询股票 k 线数据
     *
     * @param code   股票代码
     * @param time   开始时间
     * @param period 查询周期  day week month quarter year
     * @return
     */
    public static List<LineDto> queryStockLine(String code, long time, String period) {
        String format = StrUtil.format(kline, code, time, period);
        HttpRequest request = HttpUtil.createGet(format);
        request.header("Cookie", cookie);
        HttpResponse execute = request.execute();
        String body = execute.body();
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONObject data = jsonObject.getJSONObject("data");
        // 数据以及对应的列名称 item 数据列表 column 数据列 JSONArray column = data.getJSONArray("column");
        JSONArray item = data.getJSONArray("item");
        List<LineDto> builds = Lists.newArrayList();
        for (int i = 0; i < item.size(); i++) {
            builds.add(LineDto.build(item.getJSONArray(i)));
        }
        // 按照时间倒序排列 打印 k 线数据信息
        List<LineDto> sorted = builds.stream().sorted(Comparator.comparing(LineDto::getTimestamp).reversed()).collect(Collectors.toList());
        for (LineDto o : sorted) {
            List<Object> objects = Lists.newArrayList(o.getTimestamp(), o.getPercent(), o.getOpen(), o.getClose(), o.getHigh(), o.getLow(), o.getPb(), o.getPe(), o.getFloatCapital());
            // List<String> dtoList = objects.stream().map(Object::toString).collect(Collectors.toList());
            // System.out.println(CollUtil.join(dtoList, "\t"));
        }
        return sorted;
    }

    /**
     * 选股模型
     */
    public static void stockModel(StockInfo stock, List<LineDto> dtos) {
        if (CollUtil.isEmpty(dtos) || dtos.size() < 3) {
            return;
        }
        // 获取最近的一条数据
        LineDto dto = dtos.get(0);
        BigDecimal capital = dto.getFloatCapital();
        BigDecimal pe = dto.getPe();
        BigDecimal pcf = dto.getPcf();
        // 最近三周的涨跌幅
        BigDecimal p1 = dto.getPercent();
        BigDecimal p2 = dtos.get(1).getPercent();
        BigDecimal p3 = dtos.get(2).getPercent();
        // 判断股票市值, 选择市值大于 50亿元，小于 300亿流动市值的股票
        if (NumberUtil.isGreater(capital, BigDecimal.valueOf(300)) || NumberUtil.isLess(capital, BigDecimal.valueOf(50))) {
            return;
        }
        // 判断市盈率，小于 15 倍的 或者大于 50倍的排除
        if (NumberUtil.isGreater(pe, BigDecimal.valueOf(50)) || NumberUtil.isLess(pe, BigDecimal.valueOf(15))) {
            return;
        }
        // 判断市现率，小于 25 即可，数据越大表示公司现金流压力较大
        if (NumberUtil.isGreater(pcf, BigDecimal.valueOf(25))) {
            return;
        }
        // 近三周涨幅连续递增,涨幅大于0，且小于10，买在起点
        if (NumberUtil.isGreater(p1, BigDecimal.valueOf(0))
                && NumberUtil.isLess(p1, BigDecimal.valueOf(10))
                && NumberUtil.isGreater(p1, p2)
                && NumberUtil.isGreater(p2, p3)
        ) {
            System.out.println("选中 " + stock.getId() + " " + stock.getName());
        }


    }


}
