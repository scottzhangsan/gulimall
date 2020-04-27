package com.atguigu.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
/**
 * 二级分类的vo
 *
 */
public class Catalog2Vo {

    private String catalog1Id ;  //一级分类的ID
    private List<Catalog3Vo> catalog3List ;
    private String id ;
    private String name ;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class Catalog3Vo {
        private String catalog2Id ;
        private String id ;
        private String name ;  //三级分类的名称
    }
}
