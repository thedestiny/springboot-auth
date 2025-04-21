package com.platform.service;

import com.platform.pojo.dto.BaseInfoDto;
import com.platform.pojo.req.ReservationReq;

public interface ReservationService {


    BaseInfoDto createConsumerReservation(ReservationReq req);


}
