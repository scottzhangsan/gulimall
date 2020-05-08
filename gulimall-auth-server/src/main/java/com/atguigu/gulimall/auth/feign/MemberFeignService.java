package com.atguigu.gulimall.auth.feign;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.vo.UserLoginVo;
import com.atguigu.gulimall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("gulimall-member")
public interface MemberFeignService {
    @PostMapping("/member/member/register")
    R register(UserRegisterVo vo) ;

    @PostMapping("/member/member/login")
    R login(UserLoginVo vo) ;
}
