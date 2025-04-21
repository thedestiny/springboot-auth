package com.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.entity.ReservationDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户预约明细表 Mapper 接口
 * </p>
 *
 * @author kaiyang
 * @since 2024-11-28
 */
public interface ReservationDetailMapper extends BaseMapper<ReservationDetail> {

    /**
     * 查询用户预约明细
     *
     * @param id 用户预约明细主键
     * @return 用户预约明细
     */
    ReservationDetail selectReservationDetailById(Long id);

    /**
     * 查询用户预约明细列表
     *
     * @param reservationDetail 用户预约明细
     * @return 用户预约明细集合
     */
    List<ReservationDetail> selectReservationDetailList(ReservationDetail reservationDetail);

    /**
     * 新增用户预约明细
     *
     * @param reservationDetail 用户预约明细
     * @return 结果
     */
    int insertReservationDetail(ReservationDetail reservationDetail);

    // 批量插入预约明细
    int insertEntityList(@Param("detailList")List<ReservationDetail> detailList);

    /**
     * 修改用户预约明细
     *
     * @param reservationDetail 用户预约明细
     * @return 结果
     */
    int updateReservationDetail(ReservationDetail reservationDetail);

    /**
     * 删除用户预约明细
     *
     * @param id 用户预约明细主键
     * @return 结果
     */
    int deleteReservationDetailById(Long id);

    /**
     * 批量删除用户预约明细
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteReservationDetailByIds(Long[] ids);

    int deleteByResId(String pid);

    int deleteByResIdList(@Param("pidList") List<String> pidList);


}
