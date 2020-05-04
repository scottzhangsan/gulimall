package com.atguigu.gulimall.product.vo;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {

    private SkuInfoEntity skuInfo ;

    private List<SkuImagesEntity> images ;

    private SpuInfoDescEntity  desc ;

    private List<SkuItemSaleAttrVo> attrs ;

    private List<SpuItemAttrGroupVo> groupAttrs ;





}
