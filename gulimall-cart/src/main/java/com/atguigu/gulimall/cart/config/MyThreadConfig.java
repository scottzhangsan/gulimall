package com.atguigu.gulimall.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class MyThreadConfig {

    /**
     * 创建固定的线程池
     * @param pool
     * @return
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties pool){
        return new ThreadPoolExecutor(pool.getCoreSize(),pool.getMaxSize(),pool.getKeepAliveTime(), TimeUnit.SECONDS
        ,new LinkedBlockingDeque<>(pool.getWorkQueueSize()), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()) ;
    }
}
