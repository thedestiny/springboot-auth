package com.platform.productserver.redpkg.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Lists;
import com.platform.authcommon.utils.IdGenUtils;
import com.platform.productserver.redpkg.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 群红包实现
 */
@Slf4j
@Service
public class GroupRedPkg extends AbstractRedPkgService implements RedPkgService {

    // 最低分配红包金额
    private static final BigDecimal LIMIT_AMT = BigDecimal.valueOf(0.01);
    // 待分配金额/未分配金额的倍数
    private static final Integer RATE = 2;

    @Override
    public Boolean sendRedPkg(SendPkgReq pkgReq) {
        // 红包单号 红包数量 红包总金额
        String orderNo = pkgReq.getOrderNo();
        Integer num = pkgReq.getNum();
        BigDecimal total = pkgReq.getTotal();

        List<RedPkgNode> nodeList = Lists.newArrayList();
        // 群红包平分模式
        if (RedPkgEnum.GROUP_AVERAGE.type.equals(pkgReq.getRedType())) {
            handleAverageRedPkg(orderNo, num, total, nodeList);
        }
        // 群红包随机模式
        if (RedPkgEnum.GROUP_RANDOM.type.equals(pkgReq.getRedType())) {
            // 已经分配的金额
            handleRedPkg(orderNo, num, total, nodeList);
        }
        // 红包列表乱序
        Collections.shuffle(nodeList);
        // 存入缓存中
        saveRedPkg2Redis(orderNo, nodeList);
        saveRedPkg2Db(pkgReq);

        return true;
    }

    /**
     * 处理平分模式群红包
     * @param orderNo 红包单号
     * @param num 红包数量
     * @param total 红包总金额
     * @param nodeList 红包列表，需要保存再数据库
     */
    private void handleAverageRedPkg(String orderNo, Integer num, BigDecimal total, List<RedPkgNode> nodeList) {
        BigDecimal ready = BigDecimal.ZERO;
        for (int i = 0; i < num; i++) {
            String id = IdGenUtils.id();
            BigDecimal div = BigDecimal.ZERO;
            if (i == num - 1) {
                // 最后一个红包使用减法
                div = NumberUtil.sub(total, ready);
            } else {
                // 保留位数 i < num，使用比例计算
                div = NumberUtil.div(total, num, 2);
                ready = NumberUtil.add(ready, div);
            }
            RedPkgNode node = new RedPkgNode(orderNo, id, div);
            nodeList.add(node);
        }
    }

    /**
     * 处理拼手气模式
     * @param orderNo  红包单号
     * @param num      红包数量
     * @param total    红包金额
     * @param nodeList 红包列表
     */
    private static void handleRedPkg(String orderNo, Integer num, BigDecimal total, List<RedPkgNode> nodeList) {
        BigDecimal ready = BigDecimal.ZERO;
        for (int i = 0; i < num; i++) {
            String id = IdGenUtils.id();
            BigDecimal div = BigDecimal.ZERO;
            if (i == num - 1) {
                // 最后一个红包使用减法
                div = NumberUtil.sub(total, ready);
            } else {
                // 保留位数 i < num，使用比例计算
                // 计算保留多少份的最低金额
                BigDecimal mul = NumberUtil.mul(LIMIT_AMT, (num - i - 1));
                // 随机值的范围为 最低红包金额 - (红包可分金额 - 保留的最低份额 - 减去已分金额)
                BigDecimal sub = NumberUtil.sub(total, mul, ready);
                // 剩余的红包总金额不能大于剩余金额的 1 倍, 并且保留两位小数
                // 1倍 *0.5 2倍 *0.66 3倍 *0.75 4倍 *0.8
                // 计算系数
                BigDecimal coefficient = NumberUtil.div(BigDecimal.valueOf(RATE),(RATE +1));
                div = RandomUtil.randomBigDecimal(LIMIT_AMT, NumberUtil.mul(sub, coefficient)).setScale(2, RoundingMode.HALF_UP);
                ready = NumberUtil.add(ready, div);
            }
            RedPkgNode node = new RedPkgNode(orderNo, id, div);
            nodeList.add(node);
        }
    }

    @Override
    public Boolean receiveRedPkg(ReceivePkgReq pkgReq) {



        return null;
    }

    public static void main(String[] args) {


        List<RedPkgNode> nodeList = new ArrayList<>();
        handleRedPkg("12345", 10, new BigDecimal("1"), nodeList);


        for (RedPkgNode node : nodeList) {
            System.out.println(node);
        }


    }
}
