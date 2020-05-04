package com.atguigu.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.gulimall.search.config.GulimallSearchElasticsearchConfig;
import com.atguigu.gulimall.search.constant.EsProductConstant;
import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient client ;
    @Override
    public SearchResult search(SearchParam param) {
        //构建查询请求
        SearchRequest request = buildSearchRequest(param) ;
        try {
            //查询请求响应
            SearchResponse response = client.search(request, GulimallSearchElasticsearchConfig.COMMON_OPTIONS);
            log.info(response.toString());
            return buildSearchResult(response,param);
        } catch (IOException e) {
           log.error("调用es服务查询出错",e);
           return  null ;
        }


    }

    /**
     *请求响应转换
     * @param response
     * @param param
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response,SearchParam param){
        SearchResult result = new SearchResult();
        long total = response.getHits().getTotalHits().value ;
        result.setTotal(total);
        result.setPageNum(param.getPageNum());
        Long pageSize = total%param.getPageNum() == 0 ? total/param.getPageNum() : total/param.getPageNum()+1 ;
        result.setTotalPages(Integer.valueOf(pageSize.toString()));
        SearchHits hits = response.getHits();
        List<SkuEsModel> models = new ArrayList<>() ;
        if(hits != null) {
            for (SearchHit hit : hits.getHits()) {
                 SkuEsModel model = new SkuEsModel() ;
                 String value = hit.getSourceAsString() ; // 获取值
                 model = JSON.parseObject(value,SkuEsModel.class) ;
                 models.add(model) ;
            }
        }
        // TODO,待获取聚合的信息,调用远程服务获取
        result.setProducts(models);
        return result ;
    }


    /**
     * 构建es的查询请求
     *
     * @param param
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        // ES查询builder
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //1:查询条件的封装
        // 1.1 must匹配模糊查询
        if (StringUtils.isNotEmpty(param.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }
        // 1.2 filter ，按照三级分类的id查
        if (param.getCatalog3Id() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }
        //1.2按照品牌信息查
        if (CollectionUtils.isNotEmpty(param.getBrands())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("barands", param.getBrands()));
        }
        //1.2 指定属性查询
        if (CollectionUtils.isNotEmpty(param.getAttrs())) {
            for (String attr : param.getAttrs()) {
                BoolQueryBuilder nestBool = QueryBuilders.boolQuery();
                String[] values = attr.split("_");
                String attrId = values[0];
                String[] attrValues = values[1].split(":");
                nestBool.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestBool.must(QueryBuilders.termQuery("attrs.attrValue", attrValues));
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", nestBool, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }
        }
        //1.2 按照是否有库存查询
        boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        //1.2按照价格区间查询
        if (StringUtils.isNotEmpty(param.getSkuPrice())) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            String[] prices = param.getSkuPrice().split("-");
            if (prices.length == 2) {
                rangeQueryBuilder.gte(prices[0]).lte(prices[1]);
            } else if (prices.length == 1) {
                if (prices[0].startsWith("_")) {
                    rangeQueryBuilder.lte(prices[0]);
                } else if (prices[0].endsWith("_")) {
                    rangeQueryBuilder.gte(prices[0]);
                }
            }
        }
        //2:排序条件的封装，排序，分页，高亮
        //2.1排序把以前所有的条件都拿来封装
        if (StringUtils.isNotEmpty(param.getSort())){
            String[] values = param.getSort().split("_") ;
            SortOrder order = values[1].equalsIgnoreCase("asc")?SortOrder.ASC:SortOrder.DESC ;
            builder.sort(values[0],order) ;
        }
        //2.2分页
        builder.from((param.getPageNum()-1)*EsProductConstant.PRODUCT_PAGE_SIZE) ;
        builder.size(EsProductConstant.PRODUCT_PAGE_SIZE) ;
        builder.query(boolQueryBuilder) ;
        //2.3高亮
        if (StringUtils.isNotEmpty(param.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle") ;
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>") ;
            builder.highlighter(highlightBuilder) ;
        }

        System.out.println(builder.toString()+"*********");
        SearchRequest request = new SearchRequest(new String[]{EsProductConstant.PRODUCT_INDEX},builder) ;
        return request;
    }

}
