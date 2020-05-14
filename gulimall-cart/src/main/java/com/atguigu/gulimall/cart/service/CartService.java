package com.atguigu.gulimall.cart.service;

import com.atguigu.gulimall.cart.vo.CartItem;

public interface CartService {


    CartItem addCart(Long skuId,Integer num);

}
