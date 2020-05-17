package com.atguigu.gulimall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class OrderRabbitMQConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate ;

    /**
     * 自定义rabbitMq消息的转换器
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return  new Jackson2JsonMessageConverter() ;
    }

    /**
     * @PostConstruct，对象完成创建以后执行此方法
     */
    @PostConstruct
    public void initRabbitTemplate(){

        /**
         * 服务器收到消息就会进行回调
         */
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
               /**
                *
                * @param correlationData   当前发送消息的唯一的id
                * @param ack   消息是否确认送到
                * @param cause  失败的原因
                */
               @Override
               public void confirm(CorrelationData correlationData, boolean ack, String cause) {

               }
           });

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 消息的触发时机，只要消息没有正确投放给指定的队列，调用此回调函数
             * @param message  投递失败消息的详细的信息
             * @param replyCode  回复的状态码
             * @param replyText  回复的文本的内容
             * @param exchange  当时这个消息发送得那个交换机
             * @param routingKey  这个消息在哪个路由键
             *                    手动确认模式，只要我们没有明确告诉MQ货物被签收，没有ack消息就一直unacked
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            }
        });
    }
}
