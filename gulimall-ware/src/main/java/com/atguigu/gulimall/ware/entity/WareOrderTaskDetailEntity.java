package com.atguigu.gulimall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 库存工作单
 *
 *
 * 开启前照灯 ： 打开大灯，近光灯。
 *
 * 超越前方车辆：打开左转向灯，变光2次，打开又转向灯。
 *
 * 与机动车回车：打开近光灯
 *
 * 同方向近距离跟车行驶：打开近光灯
 *
 * 通过交通信号灯控制的路口：打开近光灯
 *
 * 在无照明的道路行驶：打开远光灯
 *
 * 在照明不良的道路行驶：打开远光灯
 *
 * 通过坡道：交替使用远近光灯
 *
 * 通过急弯：交替使用远近光灯
 *
 * 通过坡桥：交替使用远近光灯
 *
 * 通过拱桥：交替使用远近光灯
 *
 * 通过人行横道：交替使用远近光灯
 *
 * 通过没有交通信号灯控制的路口：交替使用远近光灯
 *
 * 路边停车：开示宽灯，开启危险报警灯
 *
 * 夜间模拟结束：关闭所有灯光。
 *
 *
 *
 */
@Data
@TableName("wms_ware_order_task_detail")
public class WareOrderTaskDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
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
