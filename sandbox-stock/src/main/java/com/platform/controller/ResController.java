package com.platform.controller;


import com.alibaba.fastjson.JSONObject;
import com.platform.pojo.base.BaseResp;
import com.platform.pojo.dto.BaseInfoDto;
import com.platform.pojo.req.ReservationReq;
import com.platform.service.ReservationService;
import com.platform.utils.RespUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "api/reservation")
public class ResController {


    @Autowired
    private ReservationService reservationService;


    /**
     * 创建预约信息
     */
    @PostMapping(value = "/create")
    public BaseResp<BaseInfoDto> reservationList(@RequestBody ReservationReq req) {
        log.info("预约 param is {}", JSONObject.toJSONString(req));
        req.setUserId(23L);
        BaseInfoDto infoDto = reservationService.createConsumerReservation(req);
        return RespUtils.success(infoDto, "预约成功");
    }

    /**
     * 预约信息查询
     */




}
