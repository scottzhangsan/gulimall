package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.SkuItemSaleAttrVo;
import com.atguigu.gulimall.product.vo.SkuItemVo;
import com.atguigu.gulimall.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SkuInfoDao;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    private SkuImagesService skuImagesService ;
    @Autowired
    private SpuInfoDescService spuInfoDescService ;
    @Autowired
    private AttrGroupService attrGroupService ;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService ;
    @Autowired
    private ThreadPoolExecutor executor;    //使用自定义的线程池


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        /**
         * key:
         * catelogId: 0
         * brandId: 0
         * min: 0
         * max: 0
         */
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and((wrapper)->{
               wrapper.eq("sku_id",key).or().like("sku_name",key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){

            queryWrapper.eq("catalog_id",catelogId);
        }

        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(catelogId)){
            queryWrapper.eq("brand_id",brandId);
        }

        String min = (String) params.get("min");
        if(!StringUtils.isEmpty(min)){
            queryWrapper.ge("price",min);
        }

        String max = (String) params.get("max");

        if(!StringUtils.isEmpty(max)  ){
            try{
                BigDecimal bigDecimal = new BigDecimal(max);

                if(bigDecimal.compareTo(new BigDecimal("0"))==1){
                    queryWrapper.le("price",max);
                }
            }catch (Exception e){

            }

        }


        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkuInfoBySpuId(Long spuId) {
        return this.baseMapper.selectList(new QueryWrapper<SkuInfoEntity>().eq("spu_id",spuId));
    }

    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
//        SkuItemVo itemVo = new SkuItemVo() ;
//        //1:获取sku的基本信息
//        SkuInfoEntity skuInfoEntity = this.getById(skuId) ;
//        Long spuId = skuInfoEntity.getSpuId();
//        Long catalogId = skuInfoEntity.getCatalogId() ;
//        itemVo.setSkuInfo(skuInfoEntity);
//        //2:获取商品的图片信息
//        List<SkuImagesEntity> skuImages= skuImagesService.getSkuImagesBySkuId(skuId);
//        itemVo.setImages(skuImages);
//        //获取所有的销售属性,依赖获取sku的信息（spuId）
//        List<SkuItemSaleAttrVo> saleAttr = skuSaleAttrValueService.getItemSaleAttrBySpuId(spuId);
//        itemVo.setAttrs(saleAttr);
//        //3:获取spu的描述信息(依赖获取sku的信息)
//        SpuInfoDescEntity desc = spuInfoDescService.getById(spuId);
//        itemVo.setDesc(desc);
//        //4：获取属性分组信息(依赖获取sku的信息)
//        List<SpuItemAttrGroupVo> groups = attrGroupService.getAttrGroupVoByCataIdAndSpuId(catalogId, spuId);
//        itemVo.setGroupAttrs(groups);


        // ********一下优化代码使用异步编程来完成

        CompletableFuture<SkuItemVo> future1 = CompletableFuture.supplyAsync(()->{
           SkuItemVo vo = new SkuItemVo() ;
           vo.setSkuInfo(this.getById(skuId));
            return  vo ;
        },executor).thenApplyAsync((result->{
            //获取获取所有的销售属性,
          SkuItemVo item1 = result ;
          Long spu = item1.getSkuInfo().getSpuId() ;
             item1.setAttrs(skuSaleAttrValueService.getItemSaleAttrBySpuId(spu) );
             return  item1 ;
        })).thenApplyAsync((result)->{
            SkuItemVo itemVo2 = result ;
            itemVo2.setDesc(spuInfoDescService.getById(itemVo2.getSkuInfo().getSpuId()));
            return  itemVo2 ;
        }).thenApplyAsync((result)->{
            SkuItemVo itemVo3 = result;
            itemVo3.setGroupAttrs(attrGroupService.getAttrGroupVoByCataIdAndSpuId(itemVo3.getSkuInfo().getCatalogId(), itemVo3.getSkuInfo().getSpuId()));
            return  itemVo3 ;
        });

        CompletableFuture<SkuItemVo> future2 = CompletableFuture.supplyAsync(()->{
            SkuItemVo vo = new SkuItemVo() ;
              vo.setImages(skuImagesService.getSkuImagesBySkuId(skuId));
            return  vo ;
        },executor);
         CompletableFuture.allOf(future1,future2) ;

         SkuItemVo temp1 =future1.get() ;
         SkuItemVo temp2 = future2.get();
        temp1.setImages(temp2.getImages());
        return temp1;
    }

}