package com.atguigu.gulimall.cart.controller;

import com.atguigu.gulimall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartController {

    @Autowired
    private CartService cartService ;


    @GetMapping("/addCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) throws Exception {
        cartService.addCart(skuId,num) ;

        return "success" ;
    }

}
