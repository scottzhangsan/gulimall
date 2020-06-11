package com.atguigu.gulimall.cart.service;

import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CartService {


    CartItem addCart(Long skuId,Integer num) throws ExecutionException, InterruptedException, Exception;

    Cart getCart();

    List<CartItem> getCartItemsByMemberId(Long memberId) ;

    void deleteCartItem(Long memberId); //删除购物车的数据

}
