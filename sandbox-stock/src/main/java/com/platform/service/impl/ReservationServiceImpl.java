package com.platform.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.platform.common.ApiException;
import com.platform.common.HttpStatus;
import com.platform.entity.*;
import com.platform.mapper.ReservationDetailMapper;
import com.platform.mapper.ReservationMapper;
import com.platform.pojo.dto.AvaiTimeDto;
import com.platform.pojo.dto.BaseInfoDto;
import com.platform.pojo.dto.MassBusyDto;
import com.platform.pojo.dto.TimeNode;
import com.platform.pojo.req.BaseReq;
import com.platform.pojo.req.ResDetailNode;
import com.platform.pojo.req.ReservationReq;
import com.platform.pojo.req.ShopItemReq;
import com.platform.pojo.res.ItemInfoRes;
import com.platform.pojo.res.MasseurDetailRes;
import com.platform.pojo.res.MasseurInfoRes;
import com.platform.pojo.res.ShopInfoRes;
import com.platform.service.ReservationService;
import com.platform.utils.IdGenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReservationServiceImpl implements ReservationService {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIM_FORMAT = "HH:mm";

    public static final String DATETIM_FORMAT = DATE_FORMAT + " " + TIM_FORMAT;
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm";

    public static final Integer QUERY_DAY = 7;
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
        // 项目id
        Long itemId = req.getItemId();
        // 从数据库查询项目信息,判断你项目是否存在
        ItemInfo item = new ItemInfo();
        // 单次只能处理一个预约
        ResDetailNode node = nodeList.get(0);
        // 预约的起止时间
        Date startTime = node.getStartTime();
        Date endTime = node.getEndTime();
        // 计算期间的分钟数
        long between = DateUtil.between(startTime, endTime, DateUnit.MINUTE);
        // 判断项目的分钟数与传入的时间是否匹配，判断项目id,店铺是否存在
        if (ObjectUtil.equal(item.getCostTime(), between)) {
            throw new ApiException("项目起止时间", HttpStatus.PARAM_ERROR);
        }
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
        // 构建订单预约明细
        List<ReservationDetail> reservationDetails = calculateReservation(res);
        detailList.addAll(reservationDetails);

        BaseInfoDto infoDto = new BaseInfoDto();
        // 事务操作
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
                    // 查询已经存在的记录，并删除
                    if (CollUtil.isNotEmpty(reservationList)) {
                        List<String> collect = reservationList.stream().map(Reservation::getResId).collect(Collectors.toList());
                        int m = reservationMapper.deleteByResIdList(collect);
                        int n = detailMapper.deleteByResIdList(collect);
                    }
                }
                // 保存预约信息和预约明细信息
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
                log.warn("保存数据异常，单号{}，detail ", e.getMessage(), e);
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
        // 首先需要根据id 查询 技师信息
        Masseur masseur = new Masseur();
        List<ItemInfo> items = masseur.getItems();
        List<ItemInfoRes> itemInfoRes = BeanUtil.copyToList(items, ItemInfoRes.class);
        // 转换技师信息
        MasseurDetailRes result = BeanUtil.copyProperties(masseur, MasseurDetailRes.class);
        // 填充店铺信息
        fillShopInfo(req, result);
        // 填充项目信息列表
        result.setItemList(itemInfoRes);
        Date now = new Date();
        // 展示未来7天的内容
        DateTime lastTime = DateUtil.offsetDay(now, QUERY_DAY);
        Reservation query = new Reservation();
        query.setStartTime(now);
        query.setEndTime(lastTime);
        query.setMasseurId(req.getMasseurId());
        // 查询未来7天时间的技师预约记录
        List<Reservation> reservations = reservationMapper.selectReservationList(query);
        List<Reservation> sorteds = reservations.stream().sorted(Comparator.comparing(Reservation::getStartTime)).collect(Collectors.toList());
        // 已经占用的时间点
        List<String> holdList = new ArrayList<>();
        for (Reservation sorted : sorteds) {
            Date st = sorted.getStartTime();
            // 当前预约的时间区间
            while (DateUtil.compare(st, sorted.getEndTime()) <= 0) {
                // 年月日-时分
                holdList.add(DateUtil.format(st, DATETIM_FORMAT));
                st = DateUtil.offsetMinute(st, 10);
            }
        }

        ShopInfoRes shopInfo = result.getShopInfo();
        // 店铺的营业开始时间和结束时间
        String startTime = DateUtil.format(shopInfo.getStartTime(), TIM_FORMAT);
        String endTime = DateUtil.format(shopInfo.getEndTime(), TIM_FORMAT);
        // 家政服务人员休假
        Set<String> holidays = handleMasseurHoliday(req.getMasseurId(), now, lastTime, result);

        // 计算忙碌节点，以家政人员为节点查询的，只能查询最近7天的内容
        List<MassBusyDto> busyList = new ArrayList<>();
        for (int i = 0; i < QUERY_DAY; i++) {
            // 当前时间 和 当前时间的日期
            DateTime tmp = DateUtil.offsetDay(now, i);
            String dat = DateUtil.format(tmp, DATE_FORMAT);
            String d1 = dat + " " + startTime; String d2 = dat + " " + endTime;
            // 店铺营业的开始时间和结束时间
            DateTime parse1 = DateUtil.parse(d1, DATETIM_FORMAT);
            DateTime parse2 = DateUtil.parse(d2, DATETIM_FORMAT);
            List<TimeNode> nodes = new ArrayList<>();
            // 循环每天的时间节点，以10分钟为时间节点进行计算
            for (int j = 0; j < 6 * 24; j++) {
                DateTime dateTime = DateUtil.offsetMinute(parse1, 10 * j);
                if (DateUtil.compare(dateTime, parse2) <= 0) {
                    // 判断时间点是否在技师的预约时间内 或者时间已经在当前时间之前
                    String ft = DateUtil.format(dateTime, DATETIM_FORMAT);
                    if (DateUtil.compare(dateTime, now) > 0) {
                        boolean fl = holdList.contains(ft) || DateUtil.compare(dateTime, now) <= 0;
                        String format = DateUtil.format(dateTime, TIM_FORMAT);
                        // 休假的节点置为休假
                        if (holidays.contains(DateUtil.format(dateTime, TIME_FORMAT))) {
                            TimeNode node = new TimeNode(format, 0, "休假");
                            nodes.add(node);
                        } else {
                            TimeNode node = new TimeNode(format, fl ? 0 : 1, "");
                            if (fl) {node.setMsg("忙碌");}
                            nodes.add(node);
                        }
                    }
                } else { break;}
            }
            MassBusyDto dto = new MassBusyDto();
            dto.setDate(dat);
            dto.setTimeList(nodes);
            busyList.add(dto);
        }
        result.setBusyList(busyList);
        return result;

    }

    private Set<String> handleMasseurHoliday(Long masseurId, Date now, DateTime lastTime, MasseurDetailRes result) {
        // 处理技师休息的时间 返回的格式为 yyyy-MM-dd HH:mm 以十分钟为单位的时间节点
        return new HashSet<>();

    }

    private void fillShopInfo(BaseReq req, MasseurDetailRes result) {
        // todo 填充服务人员所属的店铺信息
    }

    @Override
    public List<MasseurInfoRes> shopMasseurInfo(ShopItemReq req) {
        if (ObjectUtil.isEmpty(req.getShopId()) || ObjectUtil.isEmpty(req.getStartTime())) {
            throw new ApiException("开始时间不能为空", HttpStatus.PARAM_ERROR);
        }
        // 项目id 根据项目id查询技师id,技师再关联其所有的项目
        List<MasseurInfoRes> resList = Lists.newArrayList();
        // 项目id和店铺id
        Long itemId = req.getItemId();
        Long shopId = req.getShopId();
        // 首先需要查询店铺下面的所有服务人员，以及每个服务人员中的项目信息。sql
        List<Masseur> masseurs = new ArrayList<>();
        // 查询店铺信息
        ShopInfo shop = new ShopInfo();
        for (Masseur ele : masseurs) {
            // 转换服务人员信息
            MasseurInfoRes masseurInfoRes = BeanUtil.copyProperties(ele, MasseurInfoRes.class);
            // 转换项目信息
            List<ItemInfo> items = ele.getItems();
            List<ItemInfoRes> itemInfoRes = BeanUtil.copyToList(items, ItemInfoRes.class);
            moveToFirst(itemInfoRes, itemId);
            if (req.getShopId() != null) {
                // 项目时间 填充开始时间
                Long cst = itemInfoRes.get(0).getCostTime();
                fillStartTime(masseurInfoRes, req.getStartTime(), cst, shop);
            }
            masseurInfoRes.setItemList(itemInfoRes);
            resList.add(masseurInfoRes);
        }
        // 按照可预约的状态进行排序
        List<MasseurInfoRes> collect = resList.stream().sorted(Comparator.comparing(MasseurInfoRes::getIdle)).collect(Collectors.toList());
        return collect;

    }


    // 移动到第一位
    private void moveToFirst(List<ItemInfoRes> itemInfoRes, Long itemId) {
        if (itemId == null) {
            return;
        }
        ItemInfoRes tag = null;
        for (ItemInfoRes itemInfoRe : itemInfoRes) {
            if (ObjectUtil.equal(itemInfoRe.getId(), itemId)) {
                tag = itemInfoRe;
                break;
            }
        }
        // 删除目标元素并添加到首位
        itemInfoRes.remove(tag);
        itemInfoRes.add(0, tag);
    }


    /**
     * 填充服务人员的开始时间
     */
    private void fillStartTime(MasseurInfoRes masseurInfoRes, Date startTime, Long cst, ShopInfo shp) {
        // 服务人员id
        Long id = masseurInfoRes.getId();
        Reservation node = new Reservation();
        node.setMasseurId(id);
        // 预约时间点
        String date = DateUtil.format(startTime, DATE_FORMAT);
        node.setNodeTime(date);
        List<Reservation> reservations = reservationMapper.selectReservationList(node);
        if (CollUtil.isEmpty(reservations)) {masseurInfoRes.setIdle(0);return;}
        // 当前时间 年月日
        String nodeTime = DateUtil.format(startTime, DATE_FORMAT);
        // 按照开始时间顺序排列
        List<Reservation> collect = reservations.stream().sorted(Comparator.comparing(Reservation::getStartTime)).collect(Collectors.toList());
        // 店铺营业开始时间字符串
        String busStartStr = DateUtil.format(startTime, DATE_FORMAT) + " " + formatTime(shp, true);
        // 营业开始时间
        DateTime busStart = DateUtil.parse(busStartStr, DATETIM_FORMAT);
        List<Date> dateList = new ArrayList<>();
        dateList.add(busStart);
        for (Reservation reservation : collect) {
            // 匹配当前日期的预约记录
            if (StrUtil.equals(DateUtil.format(reservation.getStartTime(), DATE_FORMAT), nodeTime)) {
                dateList.add(DateUtil.offsetMinute(reservation.getStartTime(), -10));
                dateList.add(DateUtil.offsetMinute(reservation.getEndTime(), 10));
            }
        }
        // 营业结束时间 字符串以及时间
        String busEndStr = DateUtil.format(startTime, DATE_FORMAT) + " " + formatTime(shp, false);
        DateTime busEnd = DateUtil.parse(busEndStr, DATETIM_FORMAT);
        dateList.add(busEnd);
        // 可用空闲时间列表
        List<AvaiTimeDto> dtoList = new ArrayList<>();
        for (int i = 0; i < dateList.size(); i += 2) {
            AvaiTimeDto dto = new AvaiTimeDto(dateList.get(i), dateList.get(i + 1));
            if (dto.getBetween() > 0) {
                dtoList.add(dto);
            }
        }

        // 按照开始时间顺序排列
        dtoList = dtoList.stream().sorted(Comparator.comparing(AvaiTimeDto::getStartTime)).collect(Collectors.toList());
        List<AvaiTimeDto> selList = new ArrayList<>();
        for (AvaiTimeDto dto : dtoList) {
            if (dto.isBetween(startTime)) {
                selList.add(dto);
            }
        }
        for (AvaiTimeDto dto : selList) {
            // 间隔时间大于项目时间
            if (dto.getBetween() >= cst.intValue()) {
                masseurInfoRes.setStartTime(dto.getStartTime());
                masseurInfoRes.setIdle(1);
                if (DateUtil.compare(startTime, dto.getStartTime()) == 0) {
                    masseurInfoRes.setIdle(0);
                    masseurInfoRes.setStartTime(null);
                }
                return;
            }
        }

        masseurInfoRes.setIdle(2);
    }


    private String formatTime(ShopInfo shp, Boolean isStart) {
        if (shp == null || shp.getId() == null) {
            return isStart ? "09:00" : "22:00";
        } else {
            return DateUtil.format(isStart ? shp.getStartTime() : shp.getEndTime(), TIM_FORMAT);
        }
    }

}
