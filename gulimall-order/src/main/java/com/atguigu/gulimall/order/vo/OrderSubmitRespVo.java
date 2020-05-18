package com.atguigu.gulimall.order.vo;

import com.atguigu.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * 订单提交相应VO
 */
@Data
public class OrderSubmitRespVo {

    private OrderEntity orderEntity ;

    private Integer code ; // 0 代码创建订单成功

}
