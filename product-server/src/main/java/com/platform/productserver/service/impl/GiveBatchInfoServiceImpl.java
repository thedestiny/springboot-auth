package com.platform.productserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.productserver.entity.GiveBatchInfo;
import com.platform.productserver.mapper.GiveBatchInfoMapper;
import com.platform.productserver.service.GiveBatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 积分分发信息表 服务实现类
 * </p>
 *
 * @author destiny
 * @since 2023-09-11
 */

@Slf4j
@Service
public class GiveBatchInfoServiceImpl extends ServiceImpl<GiveBatchInfoMapper, GiveBatchInfo> implements GiveBatchInfoService {

    @Override
    public GiveBatchInfo queryBatchInfo(String batchNo) {
        return baseMapper.selectBatchInfo(batchNo);
    }

    @Override
    public Integer saveBatchInfo(GiveBatchInfo batchInfo) {
        return baseMapper.insert(batchInfo);
    }

    @Override
    public Integer updateBatchInfo(GiveBatchInfo batchInfo) {
        return baseMapper.updateById(batchInfo);
    }

    @Override
    public Integer updateEntityById(GiveBatchInfo batchInf) {
        return baseMapper.updateById(batchInf);
    }
}
