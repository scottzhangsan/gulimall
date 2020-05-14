package com.atguigu.gulimall.cart.vo;

import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class Cart {

    List<CartItem> items ;

    private Integer countNum ; //商品数量

    private Integer countType ; //商品类别

    private BigDecimal totalAmount ; //商品总价

    private BigDecimal reduce ; //减免价格

    public Integer getCountType() {
        int count = 0 ;
        if(CollectionUtils.isNotEmpty(this.items)){
            for (CartItem item:this.items) {
                count++ ;
            }
        }
        return count;
    }

    public Integer getCountNum() {
        int count = 0 ;
        if (CollectionUtils.isNotEmpty(this.items)){
            for (CartItem item:this.items) {
                count+=item.getCount() ;
            }
        }
        return count;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = BigDecimal.ZERO ;
        if(CollectionUtils.isNotEmpty(this.items)){
            for (CartItem item:this.items) {
                amount = amount.add(item.getTotalPrice()) ;
            }
        }
        return amount;
    }
}
