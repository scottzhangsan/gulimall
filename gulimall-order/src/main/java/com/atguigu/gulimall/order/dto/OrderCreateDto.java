package com.atguigu.gulimall.order.dto;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单创建dto
 */
@Data
public class OrderCreateDto {
     private OrderEntity oredr ;
     private List<OrderItemEntity> items ;
     private BigDecimal payPrice ;
     private BigDecimal fare ; //运费

}
