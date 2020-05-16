package com.atguigu.gulimall.cart.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("gulimall-product")
public interface ProductFeignService {

    @GetMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfoById(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skusaleattrvalue/valueslist/{skuId}")
   List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId) ;
}
