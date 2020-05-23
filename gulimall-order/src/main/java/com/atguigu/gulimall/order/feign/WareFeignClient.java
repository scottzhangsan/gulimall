package com.atguigu.gulimall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("gulimall-ware")
public interface WareFeignClient {
}
