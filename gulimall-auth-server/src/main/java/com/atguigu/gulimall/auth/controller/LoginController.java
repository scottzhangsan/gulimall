package com.atguigu.gulimall.auth.controller;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.ThirdServerFeignService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private ThirdServerFeignService thirdServerFeignService ;
    @Autowired
    private StringRedisTemplate redisTemplate ;


    @GetMapping("/sendCode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone){
        //TODO,接口防刷
        //保存的时候，首先从redis里取出看当前手机号是否保存过验证码
        String codeValue = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone) ;
        if (StringUtils.isNotEmpty(codeValue)){
            Long time = Long.valueOf(codeValue.split("_")[1]);
            if (System.currentTimeMillis()-time<60*1000){
                return  R.error(BizCodeEnume.VALID_SMS_CODE_EXCEPTION.getCode(),BizCodeEnume.VALID_SMS_CODE_EXCEPTION.getMsg());
            }
        }
       String code = UUID.randomUUID().toString().replace("-","").substring(0,5);
       //验证码保存到redis
        // 注意，60S内不能重复发送，解决办法，保存验证码的时候同时保存一下系统的当前的时间，已-分割
       redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,code+"_"+System.currentTimeMillis(),5, TimeUnit.MINUTES);
       thirdServerFeignService.sendSmsCode(phone,code) ;
       return R.ok() ;

    }
}
