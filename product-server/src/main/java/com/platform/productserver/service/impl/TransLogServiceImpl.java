package com.platform.productserver.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.platform.productserver.entity.BtransLog;
import com.platform.productserver.entity.CtransLog;
import com.platform.productserver.mapper.BtransLogMapper;
import com.platform.productserver.mapper.CtransLogMapper;
import com.platform.productserver.service.TransLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description c b 端操作日志
 * @Date 2023-09-18 3:29 PM
 */
@Slf4j
@Service
public class TransLogServiceImpl implements TransLogService {

    @Autowired
    private BtransLogMapper btransLogMapper;
    @Autowired
    private CtransLogMapper ctransLogMapper;


    @Override
    public Integer insertCtransLogs(List<CtransLog> logList) {
        if(CollUtil.isEmpty(logList)){
            return 0;
        }
        return ctransLogMapper.insertEntityList(logList);
    }

    @Override
    public Integer insertBtransLogs(List<BtransLog> logList) {
        if(CollUtil.isEmpty(logList)){
            return 0;
        }
        return btransLogMapper.insertEntityList(logList);
    }
}
