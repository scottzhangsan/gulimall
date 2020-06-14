package com.atguigu.gulimall.order.controller.web;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.gulimall.order.config.AlipayTemplate;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.PayAsyncVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 用于处理支付宝通知的controller
 */
@Controller
@Slf4j
public class OrderAliPayController {
   @Autowired
   private AlipayTemplate alipayTemplate ;
   @Autowired
   private OrderService orderService ;

    @PostMapping("payNotify")
    @ResponseBody
    public String asynNotifyHandel(PayAsyncVo payAsyncVo,HttpServletRequest request) throws UnsupportedEncodingException, AlipayApiException {

        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        //验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(), alipayTemplate.getCharset(), alipayTemplate.getSign_type());
       if (signVerified){
           //调用业务代码
           try{
               orderService.handelPayResult(payAsyncVo) ;
               return "success" ;
           }catch (Exception e){
            log.error("处理失败",e);
              return "fail"  ;
           }

       }else{
           return "fail" ;
       }

    }
}
