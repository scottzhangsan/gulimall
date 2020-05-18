package com.atguigu.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.OrderConstant;
import com.atguigu.common.utils.BeanCopyUtil;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberEntityResponseVo;
import com.atguigu.gulimall.order.dto.OrderCreateDto;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.atguigu.gulimall.order.feign.CartFeignClient;
import com.atguigu.gulimall.order.feign.MemberFeignClient;
import com.atguigu.gulimall.order.feign.ProductFeignClient;
import com.atguigu.gulimall.order.intecerptor.LoginUserInterceptor;
import com.atguigu.gulimall.order.service.OrderItemService;
import com.atguigu.gulimall.order.vo.*;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.order.dao.OrderDao;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    @Autowired
    private MemberFeignClient memberFeignClient ;
    @Autowired
    private CartFeignClient cartFeignClient ;
    @Autowired
    private StringRedisTemplate stringRedisTemplate ;

    public static ThreadLocal<MemberEntityResponseVo> memberThreadLocal = new ThreadLocal<>() ;

    @Autowired
    private ProductFeignClient productFeignClient ;
    @Autowired
    private OrderItemService orderItemService ;

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
        // 如果下面的代码要用异步的方式来执行， RequestContextHolder.getRequestAttributes() ;就无法共享，需要在后面
        //的多线程的地方进行重新设置RequestContextHolder.getRequestAttributes()

        OrderConfirmVo confirmVo = new OrderConfirmVo() ;
        MemberEntityResponseVo memberResponseVo = LoginUserInterceptor.threadLocal.get();
        List<MemberAddressVo> addresss = memberFeignClient.getMemberAddrsss(memberResponseVo.getId()) ;
        confirmVo.setAddress(addresss);
        List<OrderItemVo> items = cartFeignClient.getCheckItem(memberResponseVo.getId());
        confirmVo.setItems(items);

        //添加防重令牌
        String token = UUID.randomUUID().toString().replace("-","") ;
        String key = OrderConstant.USER_ORDER_TOKEN_PREFIX+memberResponseVo.getId() ;
        //令牌存入redis中
        stringRedisTemplate.opsForValue().set(key,token,30, TimeUnit.MINUTES);  //默认的失效的时间为30分钟

        confirmVo.setOrderToken(token);

        return confirmVo;
    }

    /**
     * 创建订单
     * @param vo
     * @return
     */
    @Override
    public OrderSubmitRespVo submit(OrderSubmitVo vo) {
        OrderSubmitRespVo respVo = new OrderSubmitRespVo() ;
        MemberEntityResponseVo memberVo = LoginUserInterceptor.threadLocal.get();
        memberThreadLocal.set(memberVo);
        // 创建订单，验证令牌，验证价格，锁库存
        String orderToken = vo.getOrderToken() ;
        String reditToken = stringRedisTemplate.opsForValue().get(OrderConstant.USER_ORDER_TOKEN_PREFIX+memberVo.getId()) ;
        // 验证token和删除token必须保证原子性 luar脚本保证原子性
        String  script = "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end" ;
        //执行脚本
        Long result = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberVo.getId()), orderToken);
        //验证token失败
        if (result == 0L){
           respVo.setCode(1);
        }else{

        }
        return respVo;
    }

    /**
     * 初始化订单
     * @return
     */
    private OrderCreateDto orderCreateDto(OrderSubmitVo vo){
        OrderCreateDto createDto = new OrderCreateDto() ;
        String orderSn = IdWorker.getTimeId(); //获取订单号
        //初始化 订单相关信息
        OrderEntity orderEntity = new OrderEntity() ;
        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberId(memberThreadLocal.get().getId()); //设置用户ID
        // TODO,待调用远程方法获取用户名
        orderEntity.setMemberUsername("scott");
        //TODO, 待获取订单的总的金额，从选中的购物车中获取，需要验证金额
        orderEntity.setTotalAmount(vo.getPayPrice());
        orderEntity.setPayAmount(vo.getPayPrice());
        orderEntity.setFreightAmount(BigDecimal.ZERO); //暂时无运费
        orderEntity.setPromotionAmount(BigDecimal.ZERO); //暂时无促销价
        orderEntity.setCouponAmount(BigDecimal.ZERO);  //暂时无优惠券
        orderEntity.setDiscountAmount(BigDecimal.ZERO); //暂时没有折扣
        orderEntity.setSourceType(OrderConstant.OrderSourceType.PC.getType());
        orderEntity.setStatus(OrderConstant.OrderStatus.CREATE_NEW.getCode());
        orderEntity.setIntegration(0); //设置可获得积分
        orderEntity.setGrowth(0); //设置成长值
        // TODO ,收货人的信息待获取
        orderEntity.setReceiverCity("ShangHai");
        orderEntity.setReceiverDetailAddress("ShangHai");
        orderEntity.setReceiverName("Scott");
        orderEntity.setReceiverPhone("123456");
        orderEntity.setReceiverProvince("ShangHai");
        orderEntity.setReceiverPostCode("10010");
        orderEntity.setReceiverRegion("ShangHai");
        orderEntity.setReceiveTime(new Date());
        orderEntity.setNote("暂时无备注");
        orderEntity.setConfirmStatus(0); //订单待确认
        orderEntity.setDeleteStatus(0); //订单未删除
        orderEntity.setUseIntegration(0); //下单时使用积分为0
        createDto.setOredr(orderEntity);
        MemberEntityResponseVo memberResponseVo = LoginUserInterceptor.threadLocal.get();
        List<OrderItemVo> items = cartFeignClient.getCheckItem(memberResponseVo.getId()); //获取选中的购物车的信息
       //保存order数据
        this.baseMapper.insert(orderEntity) ;

        // 组装订单item数据
        List<OrderItemEntity> collect = items.stream().map((item) -> {
            OrderItemEntity orderItemEntity = new OrderItemEntity();
            orderItemEntity.setOrderId(orderEntity.getId()); //订单id
            orderItemEntity.setOrderSn(orderEntity.getOrderSn());
            R r = productFeignClient.getSkuInfoById(item.getSkuId());

            SkuInfoVo skuInfo = r.getData(new TypeReference<SkuInfoVo>() {
            });
            //设置sku属性
            orderItemEntity.setSkuId(item.getSkuId());
            orderItemEntity.setSkuName(skuInfo.getSkuName());
            // TODO,待获取
            orderItemEntity.setSkuAttrsVals(null);
            orderItemEntity.setSkuPrice(skuInfo.getPrice());
            orderItemEntity.setSkuQuantity(item.getCount());
            // TODO, spu的相关信息待获取
            orderItemEntity.setSpuId(11L);
            orderItemEntity.setSpuBrand("华为");
            orderItemEntity.setSpuName("华为");
            orderItemEntity.setSpuPic("11");
            orderItemEntity.setCategoryId(225L);

            return orderItemEntity;
        }).collect(Collectors.toList());
        orderItemService.saveBatch(collect) ;
        return  createDto ;
    }

}