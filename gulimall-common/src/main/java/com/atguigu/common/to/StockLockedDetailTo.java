package com.atguigu.common.to;

import lombok.Data;

@Data
public class StockLockedDetailTo {

    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;

    private Long wareId ; //库存ID

    private Integer lockStatus ; //库存锁定的状态
}
