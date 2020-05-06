package com.atguigu.gulimall.auth.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("gulimall-third-server")
public interface ThirdServerFeignService {

    //调用第三方服务发送验证码
    @GetMapping("/sms/send")
    R sendSmsCode(@RequestParam("phone") String phone, @RequestParam("code") String code) ;
}
