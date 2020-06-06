package com.atguigu.gulimall.cart.vo;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserInfoTo {

    private Long userId ;    // 在redis中购物车的key   redis的设计为 hash  cart:item:userId, skuId, cartItem

    private String userKey ;
}
