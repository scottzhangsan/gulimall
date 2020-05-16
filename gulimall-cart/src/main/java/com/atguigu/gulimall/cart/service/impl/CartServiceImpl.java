package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.utils.BeanCopyUtil;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.feign.ProductFeignService;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.SkuInfoVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate ;
    @Autowired
    private ProductFeignService cartFeignService ;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor ;
    @Override
    public CartItem addCart(Long skuId, Integer num) throws Exception {
        CartItem cartItem = new CartItem() ;
        BoundHashOperations<String, Object, Object> boundHashOperations = getBoundHashOperationsByKey();
        String  resultStr = (String) boundHashOperations.get(skuId.toString());
        if (StringUtils.isEmpty(resultStr)) {
            //获取商品的sku信息
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                R result = cartFeignService.getSkuInfoById(skuId);
                if (result.getCode() == 0) {
                    Object data = result.get("data");
                    String result01 = JSON.toJSONString(data) ;
                    SkuInfoVo skuInfoVo = JSON.parseObject(result01,SkuInfoVo.class) ;
                    cartItem.setCheck(true);
                    cartItem.setCount(num);
                    cartItem.setImage(skuInfoVo.getSkuDefaultImg());
                    cartItem.setPrice(skuInfoVo.getPrice());
                    cartItem.setSkuId(skuId);
                    cartItem.setTitle(skuInfoVo.getSkuTitle());
                }
            }, threadPoolExecutor);
            CompletableFuture<Void> getSkuAttrTask = CompletableFuture.runAsync(() -> {
                cartItem.setSkuAttr(cartFeignService.getSkuSaleAttrValues(skuId));
            }, threadPoolExecutor);
            //两个任务都完成以后，执行下面的操作
            CompletableFuture.allOf(getSkuInfoTask, getSkuAttrTask).get();
            boundHashOperations.put(skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        }else{
            //当前购物项的信息转换为CartItem
            CartItem item = JSON.parseObject(resultStr,CartItem.class) ;
            item.setCount(item.getCount()+num);
            boundHashOperations.put(skuId+"",JSON.toJSONString(item));
           return  item ;
        }
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
