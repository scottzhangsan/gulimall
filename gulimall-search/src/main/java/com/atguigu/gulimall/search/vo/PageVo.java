package com.atguigu.gulimall.search.vo;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class PageVo<T> {
    @NotNull
    private Integer page  ;
    @NotNull
    private Integer size ;
    //@Valid
    @NotNull
    private T obj ;

}
