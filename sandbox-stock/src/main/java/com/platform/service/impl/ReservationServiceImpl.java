package com.platform.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.platform.entity.Reservation;
import com.platform.entity.ReservationDetail;
import com.platform.mapper.ReservationDetailMapper;
import com.platform.mapper.ReservationMapper;
import com.platform.pojo.dto.BaseInfoDto;
import com.platform.pojo.req.BaseReq;
import com.platform.pojo.req.ResDetailNode;
import com.platform.pojo.req.ReservationReq;
import com.platform.pojo.res.MasseurDetailRes;
import com.platform.service.ReservationService;
import com.platform.utils.IdGenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReservationServiceImpl implements ReservationService {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIM_FORMAT = "HH:mm";

    @Autowired
    private TransactionTemplate template;

    @Autowired
    private ReservationMapper reservationMapper;

    @Autowired
    private ReservationDetailMapper detailMapper;

    @Override
    public BaseInfoDto createConsumerReservation(ReservationReq req) {
        // true 新增 false 修改
        Boolean addFlag = req.getAddFlag() == null || req.getAddFlag() == 1;
        String resId = IdGenUtils.getIdStr();
        List<ResDetailNode> nodeList = req.getNodeList();

        // 单次只能处理一个预约
        ResDetailNode node = nodeList.get(0);
        Date startTime = node.getStartTime();
        Date endTime = node.getEndTime();

        // 当前时间
        Date now = new Date();

        List<ReservationDetail> detailList = Lists.newArrayList();

        Reservation res = BeanUtil.copyProperties(node, Reservation.class);
        res.setResId(resId);
        res.setOrderId(0L);
        res.setCreateTime(now);
        res.setUserId(req.getUserId());
        res.setShopId(req.getShopId());
        res.setNodeTime(DateUtil.format(node.getStartTime(), DATE_FORMAT));
        List<ReservationDetail> reservationDetails = calculateReservation(res);
        detailList.addAll(reservationDetails);

        BaseInfoDto infoDto = new BaseInfoDto();

        Object obj = template.execute(status -> {
            try {
                // 修改时需要先删除之前的记录
                if (!addFlag) {
                    Reservation query = new Reservation();
                    query.setItemId(res.getItemId());
                    query.setShopId(res.getShopId());
                    query.setMasseurId(res.getMasseurId());
                    query.setUserId(res.getUserId());
                    query.setOrderId(0L);
                    List<Reservation> reservationList = reservationMapper.selectReservationList(query);
                    // 查询已经存在的记录
                    if (CollUtil.isNotEmpty(reservationList)) {
                        List<String> collect = reservationList.stream().map(Reservation::getResId).collect(Collectors.toList());
                        int m = reservationMapper.deleteByResIdList(collect);
                        int n = detailMapper.deleteByResIdList(collect);
                    }
                }

                int m = reservationMapper.insertReservation(res);
                int n = detailMapper.insertEntityList(detailList);
                return m > 0 && n > 0;
            } catch (DuplicateKeyException dke) {
                status.setRollbackOnly();
                log.warn("数据重复，单号{}", dke.getMessage(), dke);
                infoDto.setMsg("按摩师预约时间有冲突!");
                return false;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.warn("保存数据异常，单号{}，detail ", e.getMessage(),e);
                return false;
            }
        });

        // 判断预约结果
        if (obj instanceof Boolean && Boolean.TRUE.equals((Boolean) obj)) {
            infoDto.setResId(resId);
            infoDto.setExpireTime(DateUtil.offset(now, DateField.MINUTE, 3));
            return infoDto;
        }

        return infoDto;

    }

    // 计算预约记录
    private List<ReservationDetail> calculateReservation(Reservation ele) {
        // 预约开始时间和结束时间
        Date start = ele.getStartTime();
        Date end = ele.getEndTime();

        List<ReservationDetail> result = Lists.newArrayList();
        // 日期
        String format = DateUtil.format(start, DATE_FORMAT);
        while (DateUtil.compare(start, end) <= 0) {
            String tmp = DateUtil.format(start, TIM_FORMAT);
            // 每次添加 10 分钟
            start = DateUtil.offsetMinute(start, 10);
            ReservationDetail detail = new ReservationDetail();
            detail.setMasseurId(ele.getMasseurId());
            detail.setReservationDate(format);
            detail.setResId(ele.getResId());
            detail.setReservationTime(tmp);
            detail.setSeq(0L);
            result.add(detail);
        }
        return result;

    }

    @Override
    public MasseurDetailRes queryMasseurInfo(BaseReq req) {
        return null;


    }

}
