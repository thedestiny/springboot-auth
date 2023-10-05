package com.platform.productserver.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.productserver.entity.GiveRefundLog;
import com.platform.productserver.mapper.GiveRefundLogMapper;
import com.platform.productserver.service.GiveRefundLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 发放撤回表 服务实现类
 * </p>
 *
 * @author destiny
 * @since 2023-09-11
 */
@Slf4j
@Service
public class GiveRefundLogServiceImpl extends ServiceImpl<GiveRefundLogMapper, GiveRefundLog> implements GiveRefundLogService {


    @Override
    public List<GiveRefundLog> queryByRefundNo(String giveNo) {
        return baseMapper.selectByRefundNo(giveNo);
    }

    @Override
    public Integer insertEntity(GiveRefundLog insertLog) {
        return baseMapper.insert(insertLog);
    }
}
