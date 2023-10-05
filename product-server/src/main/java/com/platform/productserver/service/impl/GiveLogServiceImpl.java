package com.platform.productserver.service.impl;



import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.productserver.entity.GiveLog;
import com.platform.productserver.mapper.GiveLogMapper;
import com.platform.productserver.service.GiveLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 积分分发订单表 服务实现类
 * </p>
 *
 * @author destiny
 * @since 2023-09-11
 */
@Slf4j
@Service
public class GiveLogServiceImpl extends ServiceImpl<GiveLogMapper, GiveLog> implements GiveLogService {


    @Override
    public Integer insertGiveLogList(List<GiveLog> logList) {
        if(CollUtil.isEmpty(logList)){
            return 0;
        }
        return baseMapper.insertEntityList(logList);
    }

    @Override
    public Integer updateGiveLogList(List<GiveLog> logs) {
        return null;

    }

    @Override
    public GiveLog queryByGiveNo(String giveNo) {
        return baseMapper.selectByGiveNo(giveNo);
    }
}
