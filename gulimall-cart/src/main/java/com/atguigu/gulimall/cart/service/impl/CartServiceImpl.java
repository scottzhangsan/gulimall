package com.atguigu.gulimall.cart.service.impl;

import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.utils.BeanCopyUtil;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.feign.CartFeignService;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.SkuInfoVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate ;
    @Autowired
    private CartFeignService cartFeignService ;
    @Override
    public CartItem addCart(Long skuId, Integer num) {
        CartItem cartItem = new CartItem() ;
        BoundHashOperations<String, Object, Object> boundHashOperations = getBoundHashOperationsByKey();
        R result = cartFeignService.getSkuInfoById(skuId) ;
        if (result.getCode() == 0){
            Object data = result.get("data");
            SkuInfoVo skuInfoVo = BeanCopyUtil.map(data,SkuInfoVo.class);
        }
        return null;
    }

    private BoundHashOperations<String, Object, Object> getBoundHashOperationsByKey() {
        //获取用户的信息
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "" ;
        if (userInfoTo.getUserId() != null){
            cartKey = CartConstant.USER_CART_PREFIX+userInfoTo.getUserId() ;
        }else{
            cartKey = CartConstant.USER_CART_PREFIX+userInfoTo.getUserKey() ;
        }
        BoundHashOperations<String, Object, Object> boundHashOperations = redisTemplate.boundHashOps(cartKey);

        return boundHashOperations ;
    }
}
