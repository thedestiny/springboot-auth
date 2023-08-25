package com.platform.productserver.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.platform.productserver.dto.FundDto;
import com.platform.productserver.entity.EtfInfo;
import com.platform.productserver.entity.FundInfo;
import com.platform.productserver.mapper.EtfInfoMapper;
import com.platform.productserver.mapper.FundInfoMapper;
import com.platform.productserver.mapper.StockInfoMapper;
import com.platform.productserver.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        for (FundDto fund : funds) {
            FundInfo info = new FundInfo();
            BeanUtils.copyProperties(fund, info);
            FundInfo info1 = fundInfoMapper.selectById(info.getCode());
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
        for (EtfInfo fund : etfs) {
            EtfInfo info1 = etfInfoMapper.selectById(fund.getCode());
            if(ObjectUtil.isNull(info1)){
                cnt += etfInfoMapper.insert(fund);
            } else {
                cnt += etfInfoMapper.updateById(fund);
            }

        }
        return cnt;
    }
}
