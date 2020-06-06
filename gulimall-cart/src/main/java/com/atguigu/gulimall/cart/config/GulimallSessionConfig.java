package com.atguigu.gulimall.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class GulimallSessionConfig {

    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer() ;
        cookieSerializer.setCookieName("GULIMALL_SESSION"); // session name
        cookieSerializer.setDomainName("gulimal.com");  //cookie 作用域
        return cookieSerializer ;
    }

    @Bean
    public RedisSerializer createRedisSerializer(){
        return new  GenericJackson2JsonRedisSerializer() ;   //设置redis序列化
    }
}
