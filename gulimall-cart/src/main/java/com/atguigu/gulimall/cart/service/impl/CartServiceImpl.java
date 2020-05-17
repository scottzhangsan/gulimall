package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.utils.BeanCopyUtil;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.feign.ProductFeignService;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.SkuInfoVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

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


    @Override
    public Cart getCart() {
        Cart cart = new Cart() ;
        //获取登录用户的信息
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get() ;
        //需要判断当前用户是否登录
        if (userInfoTo.getUserId() != null){
            //获取当前登录用户的用户的购物车信息
            String userKey = CartConstant.USER_CART_PREFIX+userInfoTo.getUserId() ;
            BoundHashOperations<String, Object, Object> boundHashOperations = redisTemplate.boundHashOps(userKey);
            //获取所有的值
            List<Object> values = boundHashOperations.values();
            List<CartItem> cartItems1 = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(values)){
               cartItems1 = getCartItems(values);
            }
           //获取用户未登录时的浏览数据
            List<CartItem> cartItems2 = new ArrayList<>() ;
            if (userInfoTo.getUserKey() != null ){
                String key = CartConstant.USER_CART_PREFIX+userInfoTo.getUserKey() ;
                BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
                List<Object> values1 = operations.values();
                if (CollectionUtils.isNotEmpty(values1)){
                    cartItems2 = getCartItems(values1);
                }
                //清空redis中未登录的用户的数据
                operations.delete(key) ;
            }
            cartItems1.addAll(cartItems2) ;
            cart.setItems(cartItems1);

        }else{
               //获取用户未登录时的浏览数据
            List<CartItem> cartItems2 = new ArrayList<>() ;
            if (userInfoTo.getUserKey() != null ){
                String key = CartConstant.USER_CART_PREFIX+userInfoTo.getUserKey() ;
                BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
                List<Object> values1 = operations.values();
                if (CollectionUtils.isNotEmpty(values1)){
                    cartItems2 = getCartItems(values1);
                }
            }
            cart.setItems(cartItems2);
        }
        return cart;
    }

    /**'
     * 获取用户选中的购物车的信息
     * @param memberId
     * @return
     */
    @Override
    public List<CartItem> getCartItemsByMemberId(Long memberId) {
        String key = CartConstant.USER_CART_PREFIX+memberId;
        BoundHashOperations<String, Object, Object> boundHashOperations = redisTemplate.boundHashOps(key);
        //保存的是Json类型的字符串
        List<Object> values = boundHashOperations.values();
        if (CollectionUtils.isNotEmpty(values)){
            List<CartItem> items = values.stream().map((result)->{
                CartItem cartItem  =JSON.parseObject(result.toString(),CartItem.class) ;
                return  cartItem ;
            }).filter((item)-> item.getCheck()).collect(Collectors.toList());
            return items ;
        }

        return  null ;
    }

    /**
     *
     * @param values1 获取购物车的详情数据
     * @return
     */
    private List<CartItem> getCartItems(List<Object> values1) {
        List<CartItem> cartItems2;
        cartItems2 = values1.stream().map((result) -> {
            String temp = (String) result;
            CartItem cartItem = new CartItem();
            cartItem = JSON.parseObject(temp, CartItem.class);
            return cartItem;
        }).collect(Collectors.toList());
        return cartItems2;
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
