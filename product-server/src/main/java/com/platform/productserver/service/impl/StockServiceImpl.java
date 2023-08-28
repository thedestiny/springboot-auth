package com.platform.productserver.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.productserver.dto.FundDto;
import com.platform.productserver.entity.EtfInfo;
import com.platform.productserver.entity.FundInfo;
import com.platform.productserver.mapper.EtfInfoMapper;
import com.platform.productserver.mapper.FundInfoMapper;
import com.platform.productserver.mapper.StockInfoMapper;
import com.platform.productserver.service.StockService;
import com.platform.productserver.stock.TianFundUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description
 * @Date 2023-08-16 10:21 AM
 */

@Slf4j
@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockInfoMapper stockInfoMapper;
    @Autowired
    private FundInfoMapper fundInfoMapper;
    @Autowired
    private EtfInfoMapper etfInfoMapper;


    @Override
    public Integer saveFundInfoList(List<FundDto> funds) {

        Integer cnt = 0;
        // 获取基金代码list,查询数据库并构建代码和实体的映射关系
        List<String> collect = funds.stream().map(FundDto::getCode).collect(Collectors.toList());
        List<FundInfo> fundInfos = fundInfoMapper.selectBatchIds(collect);
        Map<String, FundInfo> infos = new HashMap<>();
        if(CollUtil.isNotEmpty(fundInfos)){
            infos = fundInfos.stream().collect(Collectors.toMap(FundInfo::getCode, Function.identity()));
        }

        for (FundDto fund : funds) {
            // 循环基金代码，并判断数据库中是否存在
            FundInfo info1 = infos.get(fund.getCode());
            if(ObjectUtil.isNull(info1) || StrUtil.isBlank(info1.getSellFee())){
                TianFundUtils.buySellFee(fund); // 买入卖出手续费
            }
            if(ObjectUtil.isNull(info1) || StrUtil.isBlank(info1.getManager())){
                TianFundUtils.fundInfo(fund); // 基金基本信息
            }
            FundInfo info = new FundInfo();
            BeanUtils.copyProperties(fund, info); // 存在则更新 不存在则插入
            if(ObjectUtil.isNull(info1)){
                cnt += fundInfoMapper.insert(info);
            } else {
                cnt += fundInfoMapper.updateById(info);
            }

        }
        return cnt;
    }

    @Override
    public Integer saveEtfInfoList(List<EtfInfo> etfs) {
        Integer cnt = 0;
        List<String> collect = etfs.stream().map(EtfInfo::getCode).collect(Collectors.toList());
        List<EtfInfo> fundInfos = etfInfoMapper.selectBatchIds(collect);
        Map<String, EtfInfo> infos = new HashMap<>();
        if(CollUtil.isNotEmpty(fundInfos)){
            infos = fundInfos.stream().collect(Collectors.toMap(EtfInfo::getCode, Function.identity()));
        }
        for (EtfInfo fund : etfs) {
            if(infos.containsKey(fund.getCode())){
                cnt += etfInfoMapper.updateById(fund);
            } else {
                cnt += etfInfoMapper.insert(fund);
            }
        }
        return cnt;
    }

    @Override
    public List<EtfInfo> queryEtfInfoList() {
        return etfInfoMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public Integer updateEtfInfo(EtfInfo etfInfo) {
        return etfInfoMapper.updateById(etfInfo);
    }
}
