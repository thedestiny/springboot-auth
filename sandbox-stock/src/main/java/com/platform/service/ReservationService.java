package com.platform.service;

import com.platform.pojo.dto.BaseInfoDto;
import com.platform.pojo.req.BaseReq;
import com.platform.pojo.req.ReservationReq;
import com.platform.pojo.res.MasseurDetailRes;

public interface ReservationService {


    BaseInfoDto createConsumerReservation(ReservationReq req);


    MasseurDetailRes queryMasseurInfo(BaseReq req);
}
