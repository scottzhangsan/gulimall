package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 商品详情的Controller
 */
@Controller
public class ItemController {


    @Autowired
    private SkuInfoService skuInfoService ;


    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws Exception{
       model.addAttribute("item",skuInfoService.item(skuId))  ;
        return "item" ;

    }
}
