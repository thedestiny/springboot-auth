package com.platform.service;

import com.platform.pojo.dto.BaseInfoDto;
import com.platform.pojo.req.BaseReq;
import com.platform.pojo.req.ReservationReq;
import com.platform.pojo.res.MasseurDetailRes;

public interface ReservationService {

    /**
     * 创建预约信息
     */
    BaseInfoDto createConsumerReservation(ReservationReq req);

    /**
     * 根据家政人员id 查询预约信息
     */
    MasseurDetailRes queryMasseurInfo(BaseReq req);





}
