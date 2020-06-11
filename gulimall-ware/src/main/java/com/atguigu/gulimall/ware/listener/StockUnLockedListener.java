package com.atguigu.gulimall.ware.listener;

import com.atguigu.common.constant.OrderConstant;
import com.atguigu.common.to.OrderTo;
import com.atguigu.common.to.StockLockedDetailTo;
import com.atguigu.common.to.StockLockedTo;
import com.atguigu.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.gulimall.ware.entity.WareOrderTaskEntity;
import com.atguigu.gulimall.ware.feign.OrderFeignService;
import com.atguigu.gulimall.ware.service.WareOrderTaskDetailService;
import com.atguigu.gulimall.ware.service.WareOrderTaskService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.atguigu.gulimall.ware.vo.OrderItemVo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RabbitListener(queues = "stock.release.stock.queue")  //监听的队列
@Slf4j
public class StockUnLockedListener {
    @Autowired
    private WareOrderTaskService wareOrderTaskService ;
    @Autowired
    private OrderFeignService orderFeignService ;
    @Autowired
    private WareSkuService wareSkuService ;
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService ;

    /**
     * 处理解锁库存的方法
     * @param lockedTo
     * @param message
     */
    @RabbitHandler
    public void handlerStockLockedRelease(StockLockedTo lockedTo, Message message, Channel channel) throws IOException {
        log.info("开始接收消息");
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
                    log.info("无须解锁");
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
                }

            }catch (Exception e){
                //如果其中有异常发生，需要重新放到消息队列中重新解锁
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            }

        }else{
            log.info("不用解锁");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }
    }

    @RabbitHandler
    public void handelOrderCancelRelease(OrderTo orderTo, Message message, Channel channel ) throws  Exception{
        try{
            if (orderTo!=null && OrderConstant.OrderStatus.CANCLED.getCode() == orderTo.getStatus()){
                WareOrderTaskEntity orderTaskEntity = wareOrderTaskService.getwareOrderTaskByOrderSn(orderTo.getOrderSn());
                List<WareOrderTaskDetailEntity> wareOrderTaskDetailEntities = wareOrderTaskDetailService.listWareOrderTaskDetailByTaskId(orderTaskEntity.getId());
                for (WareOrderTaskDetailEntity detailEntity:wareOrderTaskDetailEntities) {
                    Long skuId = detailEntity.getSkuId();
                    Long wareId = detailEntity.getWareId();
                    Integer num = detailEntity.getSkuNum();
                    wareSkuService.unlockStock(skuId, wareId, num);
                }
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }

}
