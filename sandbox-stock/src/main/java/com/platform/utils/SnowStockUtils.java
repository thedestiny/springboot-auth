package com.platform.utils;


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
import com.platform.dto.LineDto;
import com.platform.entity.StockInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class SnowStockUtils {

    /**
     * k 线数据获取接口列表
     */
    public static final String kline = "https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol={}&begin={}&period={}&type=before&count=-294&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance";

    // 获取stock 列表
    public final static String tmp = "https://stock.xueqiu.com/v5/stock/screener/quote/list.json?page={}&size={}&order=desc&orderby=code&order_by=symbol&market=CN&type=sh_sz";
    // cookie
    public final static String cookie =
            "device_id=2b38bc16558c7136729b7ca56964df25; s=af11m6tu48; bid=db8e4f1e50c43c68f8fff28eae72dd3f_ll3vxqz5; cookiesu=481692415870953; xq_a_token=9eb4932f3197a06d242009fa2ee386ce66c8799f; xqat=9eb4932f3197a06d242009fa2ee386ce66c8799f; xq_r_token=d7a7d3c7d70d9e116b3f0277feb01c5b9543559b; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOi0xLCJpc3MiOiJ1YyIsImV4cCI6MTcwNTg4NDA0OSwiY3RtIjoxNzA0NTI5NjYzMDc5LCJjaWQiOiJkOWQwbjRBWnVwIn0.CIAdPY7iul9bAViqfloQicQj_Q8bJqWfldd09QloqaWJA3k9GO6fLhhogXLo-hRrJSaj2xcBx0FyG1bmGouR7RhhojeIy02X4UlBOuxaZBHIMcKDbiXG7xDFXm-W5DLslRFyC2UceS1_LMaY4WLICwccui1MUiS6ADmu-enNMEV5ZuN6EE231jcHntsRhmLfS0MLy4kT2wcAFCD9NAoLbELCRG7Q1p1Mq4wvvlt_Iz2OJj9-AqQNwyeoz_fLTeoGnvNv2wsfW8pJj0DhwkvhY84OC6usRm-R78v5y9B7pHCKIPE8xvSlTwJUzNTV_Bz7ZearB5Ok0cDgMQdtY5S4Dg; u=481692415870953; is_overseas=0; Hm_lvt_1db88642e346389874251b5a1eded6e3=1704529680; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1704529680";

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
        if (data == null) {
            return resultList;
        }
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
            stock.setMarketCapital(trans2Bg(node.getBigDecimal("market_capital"))); // 市值
            stock.setFloatMarketCapital(trans2Bg(node.getBigDecimal("float_market_capital"))); // 流动市值
            stock.setAmount(trans2Bg(node.getBigDecimal("amount"))); // 成交额
            stock.setFloatShares(trans2Bg(node.getBigDecimal("float_shares"))); // 流动股本
            stock.setTotalShares(trans2Bg(node.getBigDecimal("total_shares"))); // 总股本
            stock.setPb(node.getBigDecimal("pb"));  // 市净率
            stock.setEps(node.getBigDecimal("eps"));   // 每股收益
            stock.setChg(node.getBigDecimal("chg")); // 涨跌(元)
            stock.setPeTtm(node.getBigDecimal("pe_ttm"));  // 静态市盈率
            stock.setPbTtm(node.getBigDecimal("pb_ttm"));  // 市净率(ttm)
            stock.setNetValue(node.getBigDecimal("net_value"));  // 每股净资产
            stock.setIssue(transDate(node.getLong("issue_date_ts"))); // 发行日期
            resultList.add(stock);
        }
        return resultList;
    }

    private static BigDecimal trans2Bg(BigDecimal val) {
        try {
            // 按 亿单位进行计算
            return NumberUtil.div(val.toBigInteger(), 100_000_000, 3);
        } catch (Exception e) {
            log.error("error {} , e ", val, e);
            return BigDecimal.ZERO;
        }
    }

    // 转换日期格式
    private static String transDate(Long date) {
        Date dat = new Date(date);
        return DateUtil.format(dat, "yyyy-MM-dd");
    }

    public static void main(String[] args) {

        System.out.println(transDate(1282838400000L));


//        StockInfo info = new StockInfo();
//        // info.setId("SZ301388");
//        info.setId("SH600519");
//
//        List<StockInfo> stocks = queryStockList(1, 900);
//        for (StockInfo stock : stocks) {
//            try {
//                List<LineDto> dtoList1 = queryStockLine(stock.getId(), 1691684478473L, "week");
//                stockModel(stock, dtoList1);
//            } catch (Exception e){}
//        }
//        List<LineDto> dtoList = queryStockLine("SZ002032", 1691684478473L, "week");

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

        String focus = split.get(0);
        stock.setFocus(handleFocus(focusTime.text(), focus));
        if (CollUtil.size(split) > 3) {
            DateTime parse1 = DateUtil.parse(DateUtil.date().year() + "-" + split.get(3), "yyyy-MM-dd");
            stock.setUpdateTime(parse1);
        }

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

    public static List<LineDto> queryStockLine(String code, String period) {
        DateTime date = DateUtil.date();
        return queryStockLine(code, date.getTime(), period);
    }

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
        stock.setChoice(0);
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
            // 设置选择标识
            stock.setChoice(1);
        }


    }


    public static void calculateStockModel(StockInfo node) {

        try {
            // 查询k 线数据
            List<LineDto> dtoList = queryStockLine(node.getId(), 1691684478473L, "week");
            // 计算模型
            stockModel(node, dtoList);
        } catch (Exception e) {

        }

    }


}
