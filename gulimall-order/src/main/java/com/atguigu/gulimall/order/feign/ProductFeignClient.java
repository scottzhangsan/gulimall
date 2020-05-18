package com.atguigu.gulimall.order.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-product")
public interface ProductFeignClient {

    @GetMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfoById(@PathVariable("skuId") Long skuId);
}
