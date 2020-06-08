package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemVo {
    private Long skuId ;    // skuid
    private String title ;   // skuName
    private Integer count ; //需要锁定的库存的数量

}
