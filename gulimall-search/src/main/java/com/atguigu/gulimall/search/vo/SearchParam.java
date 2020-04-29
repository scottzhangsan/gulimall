package com.atguigu.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面所有可能传递过来的查询条件
 */
@Data
public class SearchParam {

    private String keyword ; //页面传递过来的全文匹配关键字
    private Long catalog3Id ; //三级分类ID
    private String sort ; //排序条件 sort = saleCount_asc /

    private Integer hasStock ; //是否有货
    private String skuPrice ; //价格区间
    private List<Long> brands ; //品牌  ，可以多选
    private List<String> attrs ; //属性.可以多选
    private Integer pageNum ; //页码


}
