package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemVo {
    private Long skuId ;    // skuid
    private String title ;   // skuName
    private String image ;  // skuImage
    private List<String> skuAttr ;
    private BigDecimal price ;  // skuPrice
    private Integer count ; // 购买的商品的数量
    private BigDecimal totalPrice;
}
