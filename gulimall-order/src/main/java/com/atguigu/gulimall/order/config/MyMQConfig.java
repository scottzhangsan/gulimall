package com.atguigu.gulimall.order.config;

import com.atguigu.common.constant.OrderConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyMQConfig {

    /**
     * 容器中Binding ,Queue,Exchange都会自动创建，在Rabbit中没有的情况下。
     * 创建特殊队列
     * @return
     */
    @Bean
    public Queue orderDelayQueue(){
        Map<String,Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", OrderConstant.MqConstant.ORDER_EVENT_EXCHANGE);
        args.put("x-dead-letter-routing-key","order.release.key");
        args.put("x-message-ttl",60000);
        //String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        Queue queue = new Queue("order.delay.queue",true,false,false,args) ;
        return  queue ;
    }

    @Bean
    public Queue orderReleaseQueue(){
        return  new Queue("order.release.order.queue",true,false,false,null) ;
    }

    @Bean
    public Exchange orderExchange(){
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        return  new TopicExchange("order-event-exchange",true,false) ;
    }

    @Bean
    public Binding orderCreateOrderBinding(){
        return  new Binding("order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order",
                null);
    }

    @Bean
    public Binding orderReleaseOrderBinding(){
        return  new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.key",
                null);
    }
}


