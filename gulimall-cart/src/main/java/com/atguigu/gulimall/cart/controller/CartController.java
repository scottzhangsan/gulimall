package com.atguigu.gulimall.cart.controller;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService ;
    //@Autowired
    //private CartInterceptor cartInterceptor ;


    /**
     * 获取用户的购物车信息
     * @return
     */
    @GetMapping("/cart.html")
    public String getUserCartList( Model model){
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        System.out.println(userInfoTo);
        Cart cartItem = cartService.getCart();
        model.addAttribute("itemList",cartItem) ;
        return "cartList" ;
    }




    @GetMapping("/addCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, Model model) throws Exception {
        CartItem cartItem = cartService.addCart(skuId, num);
        model.addAttribute("item",cartItem) ;
        return "success" ;
    }

    /**'
     * 获取所有的购物车
     * @return
     */
    @GetMapping("/getCart")
    @ResponseBody
    public R getCart(){
        return R.ok().put("result",cartService.getCart()) ;
    }

    /**
     * 获取用户选中的购物车的信息
     * @param memberId
     * @return
     */
    @GetMapping("/getCheckItem/{memberId}")
    @ResponseBody
    public List<CartItem> getCheckItem(@PathVariable("memberId") Long memberId){
        return  cartService.getCartItemsByMemberId(memberId) ;
    }


}
