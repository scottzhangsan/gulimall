package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberEntityResponseVo;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.gulimall.auth.feign.ThirdServerFeignService;
import com.atguigu.gulimall.auth.vo.UserLoginVo;
import com.atguigu.gulimall.auth.vo.UserRegisterVo;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private ThirdServerFeignService thirdServerFeignService ;
    @Autowired
    private StringRedisTemplate redisTemplate ;
    @Autowired
    private MemberFeignService memberFeignService ;


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

    @PostMapping("/register")
    public String register(@Valid UserRegisterVo vo, BindingResult result,RedirectAttributes attributes){
       // 当有校验不通过的时候
        if (result.hasErrors()){
            Map<String,String> errors = new HashMap<>() ;
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError error:fieldErrors) {
                errors.put(error.getField(),error.getDefaultMessage()) ;
            }
            attributes.addFlashAttribute("errors",errors) ;
           return "redirect:http://auth.gulimal.com/reg.html" ;
         }

        // TODO,校验通过调用接口保存注册的数据
        memberFeignService.register(vo) ;
        return "redirect:http://auth.gulimal.com/login.html" ;
    }

    @PostMapping("/login")
    public String login(UserLoginVo loginVo, HttpSession session){
        R result = memberFeignService.login(loginVo) ;
        if (result.getCode() == 0){
            //成功
            String jsonStr = (String) result.get("result");
            MemberEntityResponseVo responseVo = JSON.parseObject(jsonStr,MemberEntityResponseVo.class);
            session.setAttribute(AuthServerConstant.LOGIN_USER,responseVo);
            return "redirect:http://gulimal.com" ;
        }else{
            return "redirect:http://auth.gulimal.com/login.html" ;
        }

    }

}
