package com.platform.orderserver.task;

import cn.hutool.core.collection.CollUtil;
import com.platform.orderserver.business.PayBusiness;
import com.platform.orderserver.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 关闭订单任务
 * @Author liangkaiyang
 * @Date 2023-07-14 5:02 PM
 */

@Slf4j
@Component
public class CloseOrderTask {

    @Autowired
    private PayBusiness payBusiness;

    /**
     * 每两分钟执行一次
     */
    @Scheduled(cron = "20 1/2 * * * ?")
    public void task(){
        List<Order> orders = queryOrderList();

        if(CollUtil.isNotEmpty(orders)){
            for (Order order : orders) {
                // 关闭订单业务
                payBusiness.closeOrder(order.getOrderNo());
                // 操作数据库修改状态

            }

        }


    }


    public List<Order> queryOrderList(){


        return new ArrayList<>();
    }



}
