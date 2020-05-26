package com.atguigu.gulimall.ware.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-order")
public interface OrderFeignService {

    @RequestMapping("order/order/status/{orderSn}")
    String getOrderStatus(@PathVariable("orderSn") String orderSn) ;
}
