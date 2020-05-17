package com.atguigu.gulimall.order.config;

import com.atguigu.gulimall.order.intecerptor.LoginUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OrderWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //配置拦截器拦截所有的请求
        registry.addInterceptor(new LoginUserInterceptor()).addPathPatterns("/**") ;
    }
}
