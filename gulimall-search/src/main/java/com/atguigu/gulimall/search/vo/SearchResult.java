package com.atguigu.gulimall.search.vo;

import com.atguigu.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;
@Data
public class SearchResult {

   private  List<SkuEsModel> products ;  //所有的商品信息、
    //如下所有的分页信息
    private Integer pageNum ;
    private Long total ;
    private Integer totalPages ;
    private List<BrandVo> brands ; //当前查询结果涉及到的所有的品牌
    private List<AttrVo> attrs ; // 当前查询结果涉及到的所有的属性
    private List<CatalogVo> catalogs ; //当前查询结果涉及到的所有的分类

}
