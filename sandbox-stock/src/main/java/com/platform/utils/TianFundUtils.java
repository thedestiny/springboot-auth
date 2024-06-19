package com.platform.utils;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.platform.dto.FundDto;
import com.platform.entity.EtfInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 天天基金
 */
@Slf4j
public class TianFundUtils {

    //
    public static final String fund_list = "http://fund.eastmoney.com/data/rankhandler.aspx?op=ph&dt=kf&ft={}&rs=&gs=0&sc=1nzf" +
            "&st=desc&sd={}&ed={}&qdii=&tabSubtype=,,,,,&pi={}&pn=100&dx=1";

    public static final String etf_list = "http://fund.eastmoney.com/data/rankhandler.aspx?op=ph&dt=fb&ft=ct&rs=&gs=0&sc=clrq&st=desc&pi=1&pn=5000";

    public static void main(String[] args) {

        // gp-股票型
        // hh-混合型
        // zq-债券型
        // zs-指数型
        // qdii-qdii
        // lof-lof
        // fof-fof
//        List<FundDto> hh = fundList(1, "hh", "混合型");
//
//        for (FundDto fundDto : hh) {
//            System.out.println(fundDto);
//
//        }

        // List<EtfInfo> etfInfos = etfInfoList();
        FundDto dd = new FundDto();
        dd.setCode("001437");
        // 001437
        buySellFee(dd);
    }

    /**
     * etf 信息列表
     * 164824
     */
    public static List<EtfInfo> etfInfoList() {

        HttpRequest request = HttpUtil.createGet(etf_list);
        request.header("Referer", "http://fund.eastmoney.com/data/fundranking.html");
        request.header("Host", "fund.eastmoney.com");
        HttpResponse execute = request.execute();
        String body = execute.body();
        String replace = body.replace("var rankData = ", "").trim();
        replace = replace.replace(";", "");
        JSONObject json = JSONObject.parseObject(replace);
        JSONArray datas = json.getJSONArray("datas");
        List<EtfInfo> dtoList = Lists.newArrayList();
        System.out.println(" \n" + datas.size() + "\n");
        for (int i = 0; i < datas.size(); i++) {
            String node = datas.getString(i).replace("\"", "");
            EtfInfo dto = new EtfInfo();
            String[] split = node.split(",");
            dto.setCode(split[0]);
            dto.setName(split[1]);
            dto.setPrice(bg(split[4]));
            dto.setUpdateDate(split[3]);
            dto.setWeek(bg(split[6]));
            dto.setMonth(bg(split[7]));
            dto.setMonth3(bg(split[8]));
            dto.setHalf(bg(split[9]));
            dto.setYear1(bg(split[10]));
            dto.setYear2(bg(split[11]));
            dto.setYear3(bg(split[12]));
            dto.setYear(bg(split[13]));
            dto.setSince(bg(split[14]));
            capEtfTradeInfo(dto);
            // 基金基本信息
            try {
                etfInfo(dto);
            } catch (Exception e) {
            }
            System.out.println("start " + i);
            dtoList.add(dto);
        }
        return dtoList;
    }

    /**
     * 查询 ETF 交易信息
     */
    public static void capEtfTradeInfo(EtfInfo dto) {

        if (StrUtil.isEmpty(dto.getCode())) {
            return;
        }
        JSONObject data = new JSONObject();
        try {
            String secid = formatStock(dto.getCode());
            // f58 基金简称
            // f43 基金最新价格
            // f170 当日涨跌幅
            // f44 最高价
            // f45 最低价
            // f46 开盘价
            // f60 昨日收盘价
            // f119 5日涨跌
            // f120 20日涨跌
            // f121 60日涨跌
            // f122 今年以来涨跌
            // String response = HttpUtil.get("http://push2.eastmoney.com/api/qt/stock/get?invt=2&fltt=2&fields=f58,f43,f170,f44,f45,f46,f60,f119,f120,f121,f122&secid=" + secid);
            String response = HttpUtil.get("https://push2.eastmoney.com/api/qt/stock/get?invt=2&fltt=2&fields=f58,f734,f107,f57,f43,f59,f169,f170,f152,f46,f60,f44,f45,f47,f48,f19,f17,f531,f15,f13,f11,f20,f18,f16,f14,f12,f39,f37,f35,f33,f31,f40,f38,f36,f34,f32,f211,f212,f213,f214,f215,f210,f209,f208,f207,f206,f161,f49,f171,f50,f86,f168,f108,f167,f71,f292,f51,f52,f191,f192,f452,f177&secid=" + secid);
            JSONObject jsonObject = JSONObject.parseObject(response);
            data = jsonObject.getJSONObject("data");
            String brief = data.getString("f58");
            dto.setBrief(brief);
            if (data.containsKey("f43") && !StrUtil.equalsAny(data.getString("f43"), "-", null, "")) {
                dto.setPrice(data.getBigDecimal("f43"));
            }
            if (data.containsKey("f170") && !StrUtil.equalsAny(data.getString("f170"), "-", null, "")) {
                dto.setRate(data.getBigDecimal("f170"));
            }

        } catch (Exception e) {
            log.error(" error  code {} data {} e ", dto.getCode(), data, e);
        }
    }

    /**
     * 转换 股票 ETF 代码
     *
     * @param code
     * @return
     */
    public static String formatStock(String code) {
        String secid = "";
        if (code.startsWith("5") || code.startsWith("1")) {
            secid = code.startsWith("5") ? "1" : "0";
        } else {
            secid = code.startsWith("6") ? "1" : "0";
        }
        return secid + "." + code;

    }

    /**
     * 查询基金数据
     */
    public static List<FundDto> fundList(Integer page, String typ, String tp) {

        List<FundDto> dtoList = Lists.newArrayList();
        String start = DateUtil.format(DateUtil.date().offset(DateField.YEAR, -1), "yyyy-MM-dd"); // 获取时间
        String end = DateUtil.format(DateUtil.date(), "yyyy-MM-dd");
        HttpRequest request = HttpUtil.createGet(StrUtil.format(fund_list, typ, start, end, page));
        request.header("Host", "fund.eastmoney.com");
        request.header("Referer", "http://fund.eastmoney.com/data/fundranking.html");
        HttpResponse execute = request.execute();
        String body = execute.body();
        String replace = body.replace("var rankData = ", "").trim();
        JSONObject json = JSONObject.parseObject(replace);
        JSONArray datas = json.getJSONArray("datas");
        if (datas == null || datas.size() == 0) {
            return dtoList;
        }
        for (int i = 0; i < datas.size(); i++) {
            String node = datas.getString(i).replace("\"", "");
            String[] split = node.split(",");
            FundDto dto = new FundDto();
            dto.setCode(split[0]);
            dto.setName(split[1]);
            dto.setUpdateDate(split[3]);
            dto.setType(tp);
            dto.setWeek(bg(split[7]));
            dto.setMonth(bg(split[8]));
            dto.setMonth3(bg(split[9]));
            dto.setHalf(bg(split[10]));
            dto.setYear(bg(split[11]));
            dto.setSince(bg(split[14]));
            dto.setFee("");
            dtoList.add(dto);
        }

        return dtoList;
    }

    private static void etfInfo(EtfInfo fund) {

        Map<String, String> listMap = fundBaseInfo(fund.getCode());

        fund.setTracking(listMap.getOrDefault("跟踪标的", "").trim().replace("该基金无跟踪标的", ""));
        fund.setBaseline(listMap.getOrDefault("业绩比较基准", "").trim());
        fund.setFundType(listMap.getOrDefault("基金类型", ""));
        fund.setCompany(listMap.getOrDefault("基金管理人", ""));
        fund.setManager(listMap.getOrDefault("基金经理人", ""));
        String issue = listMap.getOrDefault("成立日期/规模", "");
        fund.setIssue(transIssue(issue));
        String fundSize = listMap.getOrDefault("资产规模", "").split("（")[0];
        String sz = StrUtil.replace(fundSize, "亿元", "").replace(",", "");
        try {
            fund.setFundSize(new BigDecimal(sz));
        }catch (Exception e){}
        fund.setShareSize(listMap.getOrDefault("份额规模", "").split("（")[0]);
    }


    private static BigDecimal bg(String val) {

        try {
            return new BigDecimal(val);
        } catch (Exception e) {
            return null;
        }

    }


    /**
     * 获取基金的基本信息
     */
    private static Map<String, String> fundBaseInfo(String fund) {
        HttpRequest request = HttpUtil.createGet(StrUtil.format("http://fundf10.eastmoney.com/jbgk_{}.html", fund));
        HttpResponse execute = request.execute();
        String body = execute.body();
        Document parse = Jsoup.parse(body);
        Element table = parse.getElementsByTag("table").get(1);  // 获取第一个表格
        List<String> keys = capTagList(table, "th"); // 获取所有的 th
        List<String> values = capTagList(table, "td"); // 获取所有的 td
        return keys.stream().collect(Collectors.toMap(key -> key, key -> values.get(keys.indexOf(key)))); // 将两个 list 组成一个 map
    }

    private static List<String> capTagList(Element table, String tag) {
        Elements tds = table.getElementsByTag(tag);
        List<String> dts = new ArrayList<>();
        for (int i = 0; i < tds.size(); i++) {
            dts.add(tds.get(i).text());
        }
        return dts;
    }

    /**
     * 基金信息概况
     * http://fundf10.eastmoney.com/jbgk_260112.html
     */
    public static void fundInfo(FundDto fund) {
        try {
            Map<String, String> listMap = fundBaseInfo(fund.getCode());
            fund.setBaseline(listMap.getOrDefault("业绩比较基准", "").trim());
            fund.setTracking(listMap.getOrDefault("跟踪标的", "").trim().replace("该基金无跟踪标的", ""));
            fund.setCompany(listMap.getOrDefault("基金管理人", ""));
            fund.setFundType(listMap.getOrDefault("基金类型", ""));
            fund.setManager(listMap.getOrDefault("基金经理人", ""));
            String issue = listMap.getOrDefault("成立日期/规模", "");
            fund.setIssue(transIssue(issue));
            fund.setFundSize(listMap.getOrDefault("资产规模", "").split("（")[0]);
            fund.setShareSize(listMap.getOrDefault("份额规模", "").split("（")[0]);
        } catch (Exception e) {

        }


    }


    private static String transIssue(String issue) {

        try {

            String s = issue.split("/")[0].trim();
            return ReUtil.replaceAll(s, "[年月]", "-").replace("日", "");

        } catch (Exception e) {
            return "";
        }

    }


    /**
     * 买卖费率
     */
    public static void buySellFee(FundDto fund) {

        try {
            // String url1 = "http://fundf10.eastmoney.com/jjfl_002147.html";
            String response = HttpUtil.get(StrUtil.format("http://fundf10.eastmoney.com/jjfl_{}.html", fund.getCode()));
            Document body = Jsoup.parse(response);
            Element txtIn = body.getElementsByClass("txt_in").get(0);
            Elements boxs = txtIn.getElementsByClass("box");

            for (int i = 0; i < boxs.size(); i++) {
                Element element = boxs.get(i);
                String h4 = element.getElementsByTag("h4").text();
                if ("赎回费率".equalsIgnoreCase(h4)) {
                    try {
                        Element table = element.getElementsByTag("table").get(0);
                        String sellFee = sellRate(table);
                        fund.setSellFee(sellFee);
                        System.out.println("赎回费率 " + sellFee);
                    } catch (Exception e) {
                    }
                }
                if (h4.contains("申购费率")) {
                    try {
                        Element table = element.getElementsByTag("table").get(0);
                        String buyFee = buyRate(table);
                        fund.setBuyFee(buyFee);
                        System.out.println("申购费率 " + buyFee);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
        }

    }


    /**
     * 买入费率 7
     */
    private static String buyRate(Element element) {
        Element tbody = element.getElementsByTag("tbody").get(0);
        String tmp = tbody.getElementsByTag("td").get(2).text().split("\\|")[0];
        return StrUtil.trim(tmp);
    }

    /**
     * 卖出费率 8
     */
    private static String sellRate(Element element) {
        Element tbody = element.getElementsByTag("tbody").get(0);
        Elements elements = tbody.getElementsByTag("tr");
        String result = "";
        for (int i = 0; i < elements.size(); i++) {
            Elements element1 = elements.get(i).getElementsByTag("td");
            String temp = element1.get(1).text().replace("大于等于", ">=")
                    .replace("大于等于", ">=")
                    .replace("小于等于", "<=")
                    .replace("小于", "<") + ":" + element1.get(2).text();
            if (StrUtil.isNotBlank(result)) {
                result += "|";
            }
            result += temp;
        }
        return result.replace("，", "至");

    }


}
