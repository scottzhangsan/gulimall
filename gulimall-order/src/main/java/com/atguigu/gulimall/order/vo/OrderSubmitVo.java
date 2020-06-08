package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 封装订单提交的数据
 */
@Data
public class OrderSubmitVo {
     private Long addrId ; //用户的收货地址id.
     private Integer payType =1 ;   //支付的方式
     //无须提交购买的商品，去购物车在取一遍
     private  String orderToken ; //防重的令牌

     private BigDecimal payPrice ; //支付的价格
     private String note  ; //订单的备注



}
