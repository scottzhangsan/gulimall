package com.atguigu.gulimall.third.controller;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.third.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsSendController {
    @Autowired
    private SmsComponent smsComponent ;

    /**
     * 发送验证码
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/send")
    public R sendSms(@RequestParam("phone") String phone, @RequestParam("code") String code){
        smsComponent.sendSmsCode(phone,code);
        return R.ok() ;
    }



}
