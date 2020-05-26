package com.atguigu.gulimall.ware.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyMQConfig {

    /**
     * 自定义rabbitMq消息的转换器
     * @return
     */
    @Bean
    public MessageConverter messageConverter()
    {
        return  new Jackson2JsonMessageConverter() ;
    }

    @Bean
    public Exchange stockEventExchange(){
       return new TopicExchange("stock-event-exchange",true,false) ;
    }

   @Bean
    public Queue stockReleaseQueue(){
        return new Queue("stock.release.stock.queue",true,false,false,null);
    }


    /**
     * 创建延时队列
     * @return
     */
    @Bean
    public Queue stockDelayQueue(){
        Map<String,Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange","stock-event-exchange");  // 消息过期后使用的交换机，topic类型的交换机
        args.put("x-dead-letter-routing-key","stock.release") ;
        args.put("x-message-ttl",120000); //2分钟 消息的过期时间，单位毫秒
        //String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        Queue queue = new Queue("stock.delay.queue",true,false,false,args) ;
        return  queue ;
    }

    @Bean
    public Binding stockReleaseBinding(){
        return  new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release.#",
                null);
    }

    /**
     * 交换机和队列的绑定
     * @return
     */
    @Bean
    public Binding stockLockedBinding(){
        return  new Binding("stock.delay.queue",  // 目标队列
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",  //使用的交换机
                "stock.locked", //需要使用的路由键
                null);
    }


}


