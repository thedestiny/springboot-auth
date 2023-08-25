package com.platform.productserver.stock;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.platform.productserver.dto.FundDto;
import com.platform.productserver.entity.EtfInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

        List<EtfInfo> etfInfos = etfInfoList();
    }

    /**
     * etf list
     *
     * @return
     */
    public static List<EtfInfo> etfInfoList() {

        HttpRequest request = HttpUtil.createGet(etf_list);
        request.header("Host", "fund.eastmoney.com");
        request.header("Referer", "http://fund.eastmoney.com/data/fundranking.html");
        HttpResponse execute = request.execute();
        String body = execute.body();
        String replace = body.replace("var rankData = ", "").trim();
        JSONObject json = JSONObject.parseObject(replace);
        JSONArray datas = json.getJSONArray("datas");

        List<EtfInfo> dtoList = Lists.newArrayList();

        System.out.println(" \n" + datas.size() + "\n");
        for (int i = 0; i < datas.size(); i++) {
            String node = datas.getString(i).replace("\"", "");
            System.out.println(node);
            EtfInfo dto = new EtfInfo();
            String[] split = node.split(",");
            dto.setCode(split[0]);
            dto.setName(split[1]);
            dto.setBrief(capEtfBriefName(split[0]));
            dto.setUpdateDate(split[3]);
            dto.setWeek(bg(split[7]));
            dto.setMonth(bg(split[8]));
            dto.setMonth3(bg(split[9]));
            dto.setHalf(bg(split[10]));
            dto.setYear(bg(split[11]));
            dto.setSince(bg(split[14]));
            // dto.setFee(bg(split[22]));
            // 基金基本信息
            try {
                etfInfo(dto);
            } catch (Exception e) {

            }
            dtoList.add(dto);
        }
        return dtoList;
    }

    /**
     * 查询 ETF 简码信息
     */
    public static String capEtfBriefName(String code) {

        if (StrUtil.isEmpty(code)) {
            return "";
        }

        try {

            String secid = formatStock(code);
            String response = HttpUtil.get("http://push2.eastmoney.com/api/qt/stock/get?invt=2&fltt=1&fields=f58&secid=" + secid);
            JSONObject jsonObject = JSONObject.parseObject(response);
            return jsonObject.getJSONObject("data").getString("f58");
        } catch (Exception e) {
            log.error(" error  code {} e {}", code, e);
            return "";
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
     *
     * @param page 1
     * @param typ  hh
     */
    public static List<FundDto> fundList(Integer page, String typ, String tp) {

        List<FundDto> dtoList = Lists.newArrayList();
        DateTime date1 = DateUtil.date().offset(DateField.YEAR, -1);
        DateTime date2 = DateUtil.date();

        String start = DateUtil.format(date1, "yyyy-MM-dd");
        String end = DateUtil.format(date2, "yyyy-MM-dd");

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
            // log.info(node.replace("\"", ""));
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
            // dto.setFee(bg(split[22]));
            // 基金基本信息
            try {
                fundInfo(dto);
            } catch (Exception e) {

            }

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
        fund.setFundSize(listMap.getOrDefault("资产规模", "").split("（" )[0]);
        fund.setShareSize(listMap.getOrDefault("份额规模", "").split("（" )[0]);
    }


    private static BigDecimal bg(String val) {

        try {
            return new BigDecimal(val);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 基金信息概况
     * http://fundf10.eastmoney.com/jbgk_260112.html
     */
    public static void fundInfo(FundDto fund) {
        // 260112
        Map<String, String> listMap = fundBaseInfo(fund.getCode());

//        for (Map.Entry<String, String> entry : listMap.entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue());
//        }
        fund.setBaseline(listMap.getOrDefault("业绩比较基准", "").trim());
        fund.setTracking(listMap.getOrDefault("跟踪标的", "").trim().replace("该基金无跟踪标的", ""));
        fund.setFundType(listMap.getOrDefault("基金类型", ""));
        fund.setCompany(listMap.getOrDefault("基金管理人", ""));
        fund.setManager(listMap.getOrDefault("基金经理人", ""));
        String issue = listMap.getOrDefault("成立日期/规模", "");
        fund.setIssue(transIssue(issue));
        fund.setFundSize(listMap.getOrDefault("资产规模", ""));
        fund.setShareSize(listMap.getOrDefault("份额规模", ""));

    }

    private static Map<String, String> fundBaseInfo(String fund) {
        HttpRequest request = HttpUtil.createGet(StrUtil.format("http://fundf10.eastmoney.com/jbgk_{}.html", fund));
        HttpResponse execute = request.execute();
        String body = execute.body();
        Document parse = Jsoup.parse(body);
        // 获取第一个表格
        Element table = parse.getElementsByTag("table").get(1);
        List<String> keys = capTagList(table, "th");
        List<String> values = capTagList(table, "td");
        return keys.stream().collect(Collectors.toMap(key -> key, key -> values.get(keys.indexOf(key))));
    }

    private static String transIssue(String issue) {

        try {

            String s = issue.split("/")[0].trim();
            return ReUtil.replaceAll(s, "[年月]", "-").replace("日", "");

        } catch (Exception e) {
            return "";
        }

    }

    private static List<String> capTagList(Element table, String tag) {
        Elements tds = table.getElementsByTag(tag);
        List<String> dts = new ArrayList<>();
        for (int i = 0; i < tds.size(); i++) {
            dts.add(tds.get(i).text());
        }
        return dts;
    }


}
