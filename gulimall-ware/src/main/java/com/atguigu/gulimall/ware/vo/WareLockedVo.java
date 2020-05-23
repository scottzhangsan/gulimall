package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * 库存锁定相关的vo
 */
@Data
public class WareLockedVo {

    private String orderSn ; //订单号

    private Long  address ; // 收货地址ID

    private List<OrderItemVo> items ; //需要具体锁定的订单项
}
