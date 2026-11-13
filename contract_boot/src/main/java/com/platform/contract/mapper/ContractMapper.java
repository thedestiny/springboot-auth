package com.platform.contract.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.contract.entity.Contract;

public interface ContractMapper extends BaseMapper<Contract> {

    Contract selectByEsignFlowId(String flowId);

}
