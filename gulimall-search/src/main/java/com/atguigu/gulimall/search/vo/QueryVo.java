package com.atguigu.gulimall.search.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryVo {
    @NotNull
    private String name ;
    @NotNull
    private Integer age ;
}
