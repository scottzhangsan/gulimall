package com.atguigu.gulimall.product.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestVo {
    @JsonProperty("FileId")
    private String fileId ;

    private String resBillNo ;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getResBillNo() {
        return resBillNo;
    }

    public void setResBillNo(String resBillNo) {
        this.resBillNo = resBillNo;
    }

    public static void main(String[] args) {
        TestVo vo =new TestVo();
        vo.setResBillNo(null);
        System.out.println(vo.getResBillNo());
    }
}
