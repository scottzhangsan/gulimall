package com.atguigu.gulimall.auth.feign;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.dto.AccessTokenResponseDto;
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

    /**
     * 微博oauth2.0注册加登录的功能
     * @param dto
     * @return
     */
    @PostMapping("/member/member/oauth/login")
    R oauthLogin(AccessTokenResponseDto dto) ;
}
