package com.atguigu.gulimall.order.service.impl;

import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberEntityResponseVo;
import com.atguigu.gulimall.order.feign.CartFeignClient;
import com.atguigu.gulimall.order.feign.MemberFeignClient;
import com.atguigu.gulimall.order.intecerptor.LoginUserInterceptor;
import com.atguigu.gulimall.order.vo.MemberAddressVo;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.order.dao.OrderDao;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    @Autowired
    private MemberFeignClient memberFeignClient ;
    @Autowired
    private CartFeignClient cartFeignClient ;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirm() {
        OrderConfirmVo confirmVo = new OrderConfirmVo() ;
        MemberEntityResponseVo memberResponseVo = LoginUserInterceptor.threadLocal.get();
        List<MemberAddressVo> addresss = memberFeignClient.getMemberAddrsss(memberResponseVo.getId()) ;
        confirmVo.setAddress(addresss);
        List<OrderItemVo> items = cartFeignClient.getCheckItem(memberResponseVo.getId());
        confirmVo.setItems(items);
        return confirmVo;
    }

}