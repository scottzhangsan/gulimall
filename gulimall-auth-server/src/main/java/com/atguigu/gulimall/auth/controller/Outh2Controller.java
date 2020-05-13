package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.dto.AccessTokenResponseDto;
import com.atguigu.gulimall.auth.feign.MemberFeignService;

import com.atguigu.gulimall.auth.vo.MemberEntityResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

/*
第三方认证登录的ctrl
微博第三方登录认证思路
1：点击第三方微博认证的图片，跳转到微博登录认证的登录页面，
2：登录成功从微博获取code，用于的到accessToken

 */
@Controller
@Slf4j
public class Outh2Controller {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MemberFeignService memberFeignService;

    private String client_id = "48957363";  // TODO,待优化从配置文件中获取
    private String client_secret = "3ce7912bc90bf60c435b7907904c1f1a";
    private String grant_type = "authorization_code";
    private String redirect_uri = "http://scott.nat300.top/outh2.0/weibo/success";

    @GetMapping("/outh2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) {
        try {
            AccessTokenResponseDto accessTokenResponseDto = restTemplate.postForObject("https://api.weibo.com/oauth2/access_token?client_id=" + client_id + "&client_secret=" + client_secret + "&grant_type=" + grant_type + "&code=" + code + "&redirect_uri=" + redirect_uri,
                    null, AccessTokenResponseDto.class);
            System.out.println(accessTokenResponseDto);
            //ouath2.0登录成功后，如果用户从来没在系统中登录过把相关的信息保存到数据库中
            R r = memberFeignService.oauthLogin(accessTokenResponseDto);

            if (r.getCode() == 0) {
             String result = (String) r.get("result");
                MemberEntityResponseVo responseVo = JSON.parseObject(result, MemberEntityResponseVo.class);
                session.setAttribute("user",responseVo) ;
            }
            //TODO,这些最后都应该用域名替代，登录成功，应该实际跳转到首页
            return "redirect:http://localhost:20000/login.html";
        } catch (Exception e) {
            log.error("微博登录失败：", e);
            return "redirect:http://localhost:20000/login.html";
        }

    }

    @GetMapping("/test")
    public String checkoutSession(HttpSession session){
        session.setAttribute("demo","demo");
        return "login" ;
    }

}
