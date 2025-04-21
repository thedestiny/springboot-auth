package com.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.platform.entity.Reservation;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户预约表 Mapper 接口
 * </p>
 *
 * @author kaiyang
 * @since 2024-12-01
 */
public interface ReservationMapper extends BaseMapper<Reservation> {

    /**
     * 查询用户预约
     *
     * @param id 用户预约主键
     * @return 用户预约
     */
    Reservation selectReservationById(Long id);

    /**
     * 查询用户预约列表
     *
     * @param reservation 用户预约
     * @return 用户预约集合
     */
    List<Reservation> selectReservationList(Reservation reservation);



    /**
     * 新增用户预约
     *
     * @param reservation 用户预约
     * @return 结果
     */
    int insertReservation(Reservation reservation);

    // 批量插入预约记录
    int insertEntityList(@Param("detailList") List<Reservation> detailList);

    /**
     * 修改用户预约
     *
     * @param reservation 用户预约
     * @return 结果
     */
    int updateReservation(Reservation reservation);

    /**
     * 删除用户预约

     */
    int deleteReservationById(Long id);

    /**
     * 批量删除用户预约
     */
    int deleteReservationByIds(Long[] ids);

    // 查询已经过期的预约记录
    List<String> selectExpireResList(@Param("timeOut") Integer timeOut);

    // 小程序查询 使用
    int deleteByResId(String resId);


    // 小程序查询 使用
    List<Reservation> selectUserReservationList(Reservation ele);

    // 小程序下单更新预约表
    Integer updateReservationOrderIdById(@Param("orderId") Long orderId, @Param("resIdList") List<Long> resIdList);


    int deleteByResIdList(@Param("pidList") List<String> pidList);

    // 根据订单id 查询用户预约单
    List<Reservation> selectReservationByOrderIdList(@Param("pidList") List<Long> orderIdList);


    // 根据技师id 和时间查询预约数量
    Integer selectResListCnt(@Param("dat") Date now, @Param("masseurId") Long id);
    // 校验技师是否可以休假
    Integer selectReservationListForCheck(Reservation ele);
}
