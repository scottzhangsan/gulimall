package com.atguigu.gulimall.order.listener;

import com.atguigu.common.constant.OrderConstant;
import com.atguigu.common.to.OrderTo;
import com.atguigu.gulimall.order.dto.OrderCreateDto;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


/**
 * 订单取消的相关listener
 */
@RabbitListener(queues = {"order.release.order.queue"}) //用于监听队列
@Component
public class OrderReleaseListener {

    @Autowired
    private OrderService orderService ;
    @Autowired
    private RabbitTemplate rabbitTemplate ;


    @RabbitHandler
    public void releaseOrder(OrderCreateDto orderCreateDto, Message message, Channel channel) throws Exception {
        OrderEntity entity = orderService.getOrderEntityByOrderSn(orderCreateDto.getOredr().getOrderSn()) ;
       try {
           //如果订单是新建的状态，就把订单取消
           if (OrderConstant.OrderStatus.CREATE_NEW.getCode()==entity.getStatus()){
               OrderEntity orderEntity = new OrderEntity() ;
               orderEntity.setId(entity.getId());
               orderEntity.setStatus(OrderConstant.OrderStatus.CANCLED.getCode()); //取消订单
               orderService.cancelOrder(orderEntity) ;
               OrderTo orderTo = new OrderTo() ;
               BeanUtils.copyProperties(orderCreateDto.getOredr(),orderTo);
               orderTo.setStatus(OrderConstant.OrderStatus.CANCLED.getCode());
               rabbitTemplate.convertAndSend("order-event-exchange","order.release.other.key",orderTo);
               //订单释放的时候，主动发送一个释放的消息给
               channel.basicAck(message.getMessageProperties().getDeliveryTag(),false); //消息不在放入队列
           }else{
               //如果不是新建的状态就确认消息
               channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
           }
       }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true); //消息还直接放入队列
       }
    };



}
