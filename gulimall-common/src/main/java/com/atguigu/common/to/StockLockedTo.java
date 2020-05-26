package com.atguigu.common.to;

import lombok.Data;

/**
 * 库存锁定传输dto
 */
@Data
public class StockLockedTo {

    private  Long taskId ;

    private StockLockedDetailTo detail ;

}
