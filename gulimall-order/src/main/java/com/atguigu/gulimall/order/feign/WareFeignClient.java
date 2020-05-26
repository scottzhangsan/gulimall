package com.atguigu.gulimall.order.feign;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.order.vo.WareLockedVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-ware")
public interface WareFeignClient {

    @PostMapping("/ware/waresku/lock")
    R lockedSku(@RequestBody WareLockedVo lockedVo) ;
}
