package com.atguigu.gulimall.search.service;

import com.atguigu.common.to.es.SkuEsModel;

import java.util.List;

public interface ProductSaveService {

    boolean saveProduct(List<SkuEsModel> list) throws Exception ;
}
