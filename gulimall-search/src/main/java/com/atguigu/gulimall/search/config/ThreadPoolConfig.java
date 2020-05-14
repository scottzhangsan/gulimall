package com.atguigu.gulimall.search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * 自定义线程池
 */
@Configuration
public class ThreadPoolConfig {


    /**
     *
     * @return
     */

    @Bean
    public ThreadPoolExecutor createExecutor(){
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 10, 10, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(10), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
       return  executor ;
    }
}
