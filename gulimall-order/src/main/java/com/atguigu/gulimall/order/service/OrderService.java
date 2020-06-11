package com.atguigu.gulimall.order.service;

import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitRespVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-08 09:56:16
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 返回订单确认也页需要的数据
     * @return
     */
    OrderConfirmVo confirm();

    /**
     * 创建订单
     * @param vo
     * @return
     */
    OrderSubmitRespVo submit(OrderSubmitVo vo) ;

    OrderEntity getOrderEntityByOrderSn(String orderSn);

    int cancelOrder(OrderEntity orderEntity);

}

