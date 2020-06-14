package com.atguigu.gulimall.order.controller.web;

import com.alipay.api.AlipayApiException;
import com.atguigu.gulimall.order.config.AlipayTemplate;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderSubmitRespVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.RoundingMode;

@Controller
public class OrderWebController {
    @Autowired
    private OrderService orderService ;
    @Autowired
    private AlipayTemplate alipayTemplate ;

    /**
     * 回到订单的确认页
     * @param model
     * @return
     */
    @GetMapping("/toTrade")
    public String  confirmOrder(Model model){
        model.addAttribute("result",orderService.confirm());
        return "confirm" ;
    }

    @PostMapping("submitOrder")
    public String submitOrder(OrderSubmitVo vo,Model model){
        OrderSubmitRespVo submit = orderService.submit(vo);
        if (submit.getCode() ==0 ) {
            model.addAttribute("result",submit.getOrderEntity()) ;
            return "pay" ;
        }else {
            return "confirm" ;
        }
        //如果订单提交成功，返回支付的页面

    }

    @GetMapping("/toTrade11")
    public String toTrade(){

        return "confirm" ;
    }
    @ResponseBody
    @GetMapping(value = "/payOrder",produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
         PayVo payVo = new PayVo() ;
        OrderEntity entity = orderService.getOrderEntityByOrderSn(orderSn) ;
        payVo.setBody("商城测试");
        payVo.setOut_trade_no(orderSn);
        payVo.setTotal_amount(entity.getTotalAmount().setScale(2, RoundingMode.UP).toString());
        payVo.setSubject("商城测试");
        String pay = alipayTemplate.pay(payVo);
        System.out.println(pay);
        return pay;
    }


}
