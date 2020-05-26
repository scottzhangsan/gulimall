package com.atguigu.gulimall.ware.listen;

import com.atguigu.common.constant.OrderConstant;
import com.atguigu.common.to.StockLockedDetailTo;
import com.atguigu.common.to.StockLockedTo;
import com.atguigu.gulimall.ware.feign.OrderFeignService;
import com.atguigu.gulimall.ware.service.WareOrderTaskService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = "stock.release.stock.queue")  //监听的队列
public class StockUnLockedListener {
    @Autowired
    private WareOrderTaskService wareOrderTaskService ;
    @Autowired
    private OrderFeignService orderFeignService ;
    @Autowired
    private WareSkuService wareSkuService ;

    /**
     * 处理解锁库存的方法
     * @param lockedTo
     * @param message
     */
    @RabbitHandler
    public void handlerStockLockedRelease(StockLockedTo lockedTo, Message message, Channel channel) throws IOException {
        System.out.println("收到解锁库存的消息");
        StockLockedDetailTo lockedDetailTo = lockedTo.getDetail() ;
        if(lockedDetailTo != null){
            //库存工作单的ID
            Long taskId = lockedTo.getTaskId() ;
            //获取订单号
            try{
                String orderSn = wareOrderTaskService.getById(taskId).getOrderSn() ;
                //根据订单号查询订单的状态，只有是状态已取消的状态才能取消
                String orderStatus = orderFeignService.getOrderStatus(orderSn) ;
                //订单被取消的状态或者订单不存在
                if (StringUtils.isEmpty(orderStatus) || Integer.valueOf(orderStatus)== OrderConstant.OrderStatus.CANCLED.getCode()){
                    //解锁订单  skuid , wareId, lockedNum , detailId
                    //需要采用自动ack的机制
                    // TODO,当前库存工作单详情的状态只有是已锁定才能进行解锁
                    wareSkuService.unlockStock(lockedDetailTo.getSkuId(),lockedDetailTo.getWareId(),lockedDetailTo.getSkuNum()) ;
                    // 告诉rabbitMq解锁成功
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
                }else{
                    //无须解锁
                    System.out.println("无须解锁");
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
                }

            }catch (Exception e){
                //如果其中有异常发生，需要重新放到消息队列中重新解锁
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            }

        }else{
            System.out.println("不用解锁");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }
    }


}
