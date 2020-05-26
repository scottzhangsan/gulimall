package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.to.StockLockedDetailTo;
import com.atguigu.common.to.StockLockedTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.gulimall.ware.entity.WareOrderTaskEntity;
import com.atguigu.gulimall.ware.feign.ProductFeignService;
import com.atguigu.gulimall.ware.service.WareOrderTaskDetailService;
import com.atguigu.gulimall.ware.service.WareOrderTaskService;
import com.atguigu.gulimall.ware.vo.OrderItemVo;
import com.atguigu.gulimall.ware.vo.WareLockedVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    private WareOrderTaskService wareOrderTaskService ;
    @Autowired
    private WareOrderTaskDetailService taskDetailService ;
    @Autowired
    private RabbitTemplate rabbitTemplate ;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuId: 1
         * wareId: 2
         */
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            queryWrapper.eq("sku_id",skuId);
        }

        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }


        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1、判断如果还没有这个库存记录新增
        List<WareSkuEntity> entities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if(entities == null || entities.size() == 0){
            WareSkuEntity skuEntity = new WareSkuEntity();
            skuEntity.setSkuId(skuId);
            skuEntity.setStock(skuNum);
            skuEntity.setWareId(wareId);
            skuEntity.setStockLocked(0);
            //TODO 远程查询sku的名字，如果失败，整个事务无需回滚
            //1、自己catch异常
            //TODO 还可以用什么办法让异常出现以后不回滚？高级
            try {
                R info = productFeignService.info(skuId);
                Map<String,Object> data = (Map<String, Object>) info.get("skuInfo");

                if(info.getCode() == 0){
                    skuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){

            }

            wareSkuDao.insert(skuEntity);
        }else{
            wareSkuDao.addStock(skuId,wareId,skuNum);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean locked(WareLockedVo lockedVo) {
        List<OrderItemVo> items = lockedVo.getItems();
        //添加库存工作单的记录
        WareOrderTaskEntity orderTaskEntity = new WareOrderTaskEntity() ;
        orderTaskEntity.setOrderSn(lockedVo.getOrderSn());
        wareOrderTaskService.save(orderTaskEntity) ;
        boolean allLockedFlag = true ; //默认所有skuID都可以锁定库存成功
        for (OrderItemVo item:items) {
            boolean singleLockedFlag = false ; // 默认单个没有被锁定
            Long skuId = item.getSkuId() ;
            Integer num = item.getNum() ;
            //根据skuID查询有哪些仓库可以进行锁库存的操作
            List<WareSkuEntity> wares = this.baseMapper.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId));
            if(!CollectionUtils.isEmpty(wares)){
                for (WareSkuEntity entity:wares) {
                   //开始锁定库存
                    Integer result = this.baseMapper.lockStock(item.getSkuId(),item.getNum()) ;
                    //单个锁定成功
                    if (result >0){
                        //库存详情锁定成功，保存详情的信息
                        singleLockedFlag = true;
                        WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity() ;
                        detailEntity.setLockStatus(1);
                        detailEntity.setSkuId(item.getSkuId());
                        detailEntity.setSkuName("test");
                        detailEntity.setSkuNum(item.getNum());
                        detailEntity.setTaskId(orderTaskEntity.getId());
                        detailEntity.setWareId(entity.getWareId());
                        taskDetailService.save(detailEntity) ;
                        StockLockedTo lockedTo = new StockLockedTo() ;
                        StockLockedDetailTo detailTo = new StockLockedDetailTo() ;
                        BeanUtils.copyProperties(detailEntity,detailTo);
                        lockedTo.setTaskId(orderTaskEntity.getId());
                        lockedTo.setDetail(detailTo);
                        //发送消息 ，使用的交换机，使用的路由键，发送的内容
                        rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",lockedTo);
                        break;
                    }
                    //如果循环结束，singleLockeFlag 没变就表示库存没有锁定成功
                }
            }else{
              allLockedFlag = false ;
            }
            if (singleLockedFlag){
                allLockedFlag = false;
            }
        }
        return allLockedFlag;
    }

    @Override
    public int unlockStock(Long skuId, Long wareId, Integer skuNum) {
        return this.baseMapper.unlockStock(skuId,wareId,skuNum);
    }

}