package com.platform.productserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.productserver.entity.GiveRefundLog;

import java.util.List;

/**
 * <p>
 * 发放撤回表 服务类
 * </p>
 *
 * @author destiny
 * @since 2023-09-11
 */
public interface GiveRefundLogService extends IService<GiveRefundLog> {


    List<GiveRefundLog> queryByRefundNo(String giveNo);

    Integer insertEntity(GiveRefundLog insertLog);
}
