package com.atguigu.gulimall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * 设置可以允许跨域的config
 */
@Configuration
public class CorsConfig {

    //允许跨域的filter
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
        corsConfiguration.setAllowCredentials(true); //设置是否发送cookie
        corsConfiguration.setAllowedHeaders(Arrays.asList("*")); //设置允许的请求头
        corsConfiguration.setAllowedMethods(Arrays.asList("*")); //设置允许的方法
        UrlBasedCorsConfigurationSource urlConfig = new UrlBasedCorsConfigurationSource();
        urlConfig.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlConfig);
    }
}
