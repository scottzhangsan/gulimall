package com.atguigu.gulimall.cart.service;

import com.atguigu.gulimall.cart.vo.CartItem;

import java.util.concurrent.ExecutionException;

public interface CartService {


    CartItem addCart(Long skuId,Integer num) throws ExecutionException, InterruptedException, Exception;

}
