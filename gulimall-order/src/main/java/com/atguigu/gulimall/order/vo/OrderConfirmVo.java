package com.atguigu.gulimall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单确认vo
 */

public class OrderConfirmVo {
    @Getter @Setter
    private List<MemberAddressVo> address ;
    @Getter @Setter
    private List<OrderItemVo> items ;
    private BigDecimal totalPrice ;
    private BigDecimal payPrice ;
    @Getter @Setter
    private String orderToken ; //订单的防止重复提交的令牌

    /**
     * 总的金额
     * @return
     */
    public BigDecimal getTotalPrice() {
       BigDecimal  zero = BigDecimal.ZERO;
       if (CollectionUtils.isNotEmpty(items)){
           for (OrderItemVo item: items) {
               BigDecimal price = new BigDecimal(item.getCount().toString()).multiply(item.getPrice()) ;
               zero = zero.add(price);
           }
       }
       return  zero ;
    }

    /**
     * 实付金额
     * @return
     */
    public BigDecimal getPayPrice() {
        return getTotalPrice();
    }
}
