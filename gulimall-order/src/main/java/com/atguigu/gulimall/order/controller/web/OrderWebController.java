package com.atguigu.gulimall.order.controller.web;

import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class OrderWebController {
    @Autowired
    private OrderService orderService ;

    /**
     * 回到订单的确认页
     * @param model
     * @return
     */
    @GetMapping("/confirmOrder")
    public String  confirmOrder(Model model){
        model.addAttribute("result",orderService.confirm());
        return "confirm" ;
    }

    @PostMapping("submitOrder")
    public String submitOrder(OrderSubmitVo vo){
        orderService.submit(vo) ;
        //如果订单提交成功，返回支付的页面
        return "pay" ;
    }


}
