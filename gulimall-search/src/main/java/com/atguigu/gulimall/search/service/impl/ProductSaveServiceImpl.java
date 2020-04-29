package com.atguigu.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.gulimall.search.config.GulimallSearchElasticsearchConfig;
import com.atguigu.gulimall.search.constant.EsProductConstant;
import com.atguigu.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    private RestHighLevelClient client ;
    @Override
    public boolean saveProduct(List<SkuEsModel> list) throws Exception {
        BulkRequest bulkRequest = new BulkRequest() ;
        for (SkuEsModel model:list) {
            IndexRequest indexRequest = new IndexRequest(EsProductConstant.PRODUCT_INDEX);
            indexRequest.id(model.getSkuId().toString()) ;
            String s = JSON.toJSONString(model);
            bulkRequest.add(indexRequest) ;
        }
        BulkResponse bulk = client.bulk(bulkRequest, GulimallSearchElasticsearchConfig.COMMON_OPTIONS);

        boolean upFailure = bulk.hasFailures() ;
        //上架的商品信息
        List<String> collect = Arrays.stream(bulk.getItems()).map((item) -> item.getId()).collect(Collectors.toList());
        log.info("商品上架的ID,{}",collect);
        return upFailure ;
    }
}
