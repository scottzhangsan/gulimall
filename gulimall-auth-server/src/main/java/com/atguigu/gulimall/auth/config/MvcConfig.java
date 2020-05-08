package com.atguigu.gulimall.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig {

    @Bean
    public WebMvcConfigurer webMvcConfigurer(){
        return new WebMvcConfigurer() {
//            @Override
//            public void addResourceHandlers(ResourceHandlerRegistry registry) {
//                registry.addResourceHandler("/reg.html").addResourceLocations("/reg.html");
//            }
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {

                //默认地址（可以是页面或后台请求接口）
                registry.addViewController("/reg.html").setViewName("reg");
                registry.addViewController("/login.html").setViewName("login");
                //设置过滤优先级最高
                registry.setOrder(Ordered.HIGHEST_PRECEDENCE);

            }

        };
    }

}
