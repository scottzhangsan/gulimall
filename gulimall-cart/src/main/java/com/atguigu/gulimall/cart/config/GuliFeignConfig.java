package com.atguigu.gulimall.cart.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration

/**
 * 解决fegin的远程的调用丢失请求头的问题
 */
public class GuliFeignConfig {

   // @Bean
    public RequestInterceptor requestInterceptor(){

        /**
         * 同理也可以获取其他的请求头的信息
         */
        return new RequestInterceptor() {
           @Override
           public void apply(RequestTemplate requestTemplate) {
               // 利用 RequestContextHolder拿到刚进来的这个请求 ，底层使用ThreadLocal来实现
               ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
               HttpServletRequest request = requestAttributes.getRequest();
               //同步请求头的数据
               String cookie = request.getHeader("Cookie"); //获取请求头中的Cookie字段
               requestTemplate.header("Cookie",cookie) ;
           }
       } ;
    }
}
