package com.platform.dto;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONArray;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 股票k 线图数据
 */
@Data
@Builder
public class LineDto implements Serializable {

    // 交易时间
    private String timestamp;
    // 成交量，即成交股数
    private BigDecimal volume;
    // 开盘
    private BigDecimal open;
    // 最高
    private BigDecimal high;
    // 最低
    private BigDecimal low;
    // 收盘
    private BigDecimal close;
    // 变更
    private BigDecimal chg;
    // 百分比
    private BigDecimal percent;
    // 换手率
    private BigDecimal turn;
    // 成交金额(亿元)
    private BigDecimal amount;
    // 市盈率
    private BigDecimal pe;
    // 市净率
    private BigDecimal pb;
    // 市销率
    private BigDecimal ps;
    // 实现率
    private BigDecimal pcf;
    // 流动市值
    private BigDecimal floatCapital;

    /**
     * 0-timestamp
     * 1-volume
     * 2-open
     * 3-high
     * 4-low
     * 5-close
     * 6-chg
     * 7-percent
     * 8-turnoverrate
     * 9-amount
     * 12-pe
     * 13-pb
     * 14-ps
     * 15-pcf
     *
     * @return
     */
    public static LineDto build(JSONArray ele) {
        Long aLong = ele.getLong(0);
        String date = DateUtil.format(DateUtil.date(aLong), "yyyy-MM-dd");
        LineDto dto = LineDto.builder()
                .timestamp(date).volume(bgs(ele, 1)).open(bg(ele, 2)).high(bg(ele, 3)).low(bg(ele, 4)).close(bg(ele, 5))
                .chg(bg(ele, 6)).percent(bg(ele, 7)).turn(bg(ele, 8)).amount(bgs(ele, 9))
                .pe(bg(ele, 12)).pb(bg(ele, 13)).ps(bg(ele, 14)).pcf(bg(ele, 15))
                .build();
        BigDecimal turn = dto.getTurn();
        BigDecimal amount = dto.getAmount();
        // 换算成流通市值
        if (NumberUtil.isGreater(turn, BigDecimal.ZERO)) {
            BigDecimal div = NumberUtil.div(amount, turn, 2);
            div = NumberUtil.div(div, 1000000, 2); // 转换单位为 亿元
            dto.setFloatCapital(div);
        }

        return dto;

    }

    public static BigDecimal bg(JSONArray ele, Integer idx) {
        return ele.getBigDecimal(idx);
    }

    public static BigDecimal bgs(JSONArray ele, Integer idx) {
        BigDecimal bg = bg(ele, idx);

        return NumberUtil.div(bg.toBigInteger(), 100_000_000, 3);
    }
}
