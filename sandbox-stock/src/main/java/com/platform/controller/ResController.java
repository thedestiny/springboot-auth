package com.platform.controller;


import com.alibaba.fastjson.JSONObject;
import com.platform.common.ApiException;
import com.platform.common.HttpStatus;
import com.platform.pojo.base.BaseResp;
import com.platform.pojo.dto.BaseInfoDto;
import com.platform.pojo.req.BaseReq;
import com.platform.pojo.req.ReservationReq;
import com.platform.pojo.res.MasseurDetailRes;
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
     * 展示一个家政人员 预约信息查询
     */
    @PostMapping(value = "/info")
    public BaseResp<MasseurDetailRes> masseurDetail(@RequestBody BaseReq req) {
        if (req.getMasseurId() == null) {
            throw new ApiException("人员id 不能为空", HttpStatus.PARAM_ERROR);
        }
        MasseurDetailRes detailRes = reservationService.queryMasseurInfo(req);
        return RespUtils.success(detailRes);
    }


}
