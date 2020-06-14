package com.atguigu.gulimall.order.config;


import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

//@ConfigurationProperties(prefix = "alipay")
//@PropertySource("classpath:alipay.properties")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2016092700604050";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDSJe8+LHOedVEbx1Jrc03fMHdofD0NesWo1IwzRyrC1yHu0RQDbBEeNfrRLz+Za4k8fJDlFViyu/8Uh7RiurB+G6qNE+lY2z5h8042T5ru7fLPDbZxntUFMBGFLANESMEvMqVvspogtkCidPZAEGagvt0iFk7pIoEgVv/VB5Iu4FRkMEv+bL5aOl/Rr2WG/EjRVppV5m2Y+Rm3FjnV3kSVE+s/4hN+N096RAfT4l+uHFty4oMtc7NoFP+VHDNLNRaO8E1FjOQQYNhAhM+YCxYQwBC1Me08LQMOdDbRre9doagQQerqLaKMISpf08qovANLuQn65th5P4XTMwN6C90xAgMBAAECggEBAL6MuSd2JQxcskPu83QM5GJpTwzGApITbeENgWcJs+NiMRmcKXqE5eyv45MENbYGhTT8emHOflPrFXtnJZCUWW6v5XDwyhjRC/rDJHsoqYLW7eEDPRg/fkaq9Gf9paSoF8uVL99dYlYhC/CC/7rNZFGDX34GHl1U9SL+4BEpQUsQZohzsG33IgOwD6WlaFKWmnYX4bxrxPUlCbFMHSu4IHz0VS56yuAZ2eHT2Zd2955JO/x0hXGtsdAVEVGVgaYd+JU8qW8NJmYQtooQ/8gojn2xSxqzcUrAnt4q10J1VaFX1mIpm9J498OWpoNkvVjvZDXJDCXTY6katj6WGqBGUnECgYEA+eMwYvLuQPEXB2BFx37akNOV9L1A6lB7OGzyQ4P0btblbn7iPvNisb3Ed/jCsoqDcNc7DmlSa5o/kjV8UFDB0q5fCZmXFeehIC9iyWpnvZcoxIrNvVIDEfQbi3ntsY68UgssjQwITMJg25ClDu6rinz+dSp14iJ7cIcFCMievY0CgYEA10nlS9uc9K+K5yL0t4WdMWhnKsnH5bLdGXE7ThHA+jjnKPTZlkeqnAGDSD5XkTxAm0hiMGKaCNgjiqXGpyNgnyLOMW/5R6atru5DFGcG8mskoALilR3CsvdH6NeALFdA317JuBfX17jcvE8ByiEzvqodTF0X8wPvdELBsZXj2zUCgYEAmlL+nP1euuipyjpgndXv8aVaZO1EW/EcSDqwW5XwrUs2IklmPG5fM1n5LgaJZ243wsaJiFrvNdjrDcbOYWOecBiURP9/2V1VmuTo4289Zk8mNEtsyFEP6FSTdk30x8VY9GpGN02JCq641qC77z7i3gJXu9BfQSr+hP4907DiqDkCgYA2mLrZnfwT4WCsdSTyVaQjn4JqS3myOsXvadPMXJw/2KZmsJFtlwchQRD2BTlXz5vixVyBQMAndgitZJUYVdRvBvibePYSwaZSMoI4blhIqftDyPeFMWLq1+MYnW4X0JjXDMbDNOi8AFmaN78Qb5MbyzBa6VDlhB35seRFEOoT8QKBgFynCM4TeA5KeDHbIG41AW03+P4FQdbjCiVIEuJxNHU7epO5lpN8SxWMiTZ/5ZJRnto93QzrXh/7FnysIVdaRdudWLBse9N6kzbRVuqHa5ZShj+pAKWrHdVMWobqVbuoQPdpnW1sb6RUqGKsj0VqxG6XdShI/sDUiAkCrOCT8up3";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0iXvPixznnVRG8dSa3NN3zB3aHw9DXrFqNSMM0cqwtch7tEUA2wRHjX60S8/mWuJPHyQ5RVYsrv/FIe0YrqwfhuqjRPpWNs+YfNONk+a7u3yzw22cZ7VBTARhSwDREjBLzKlb7KaILZAonT2QBBmoL7dIhZO6SKBIFb/1QeSLuBUZDBL/my+Wjpf0a9lhvxI0VaaVeZtmPkZtxY51d5ElRPrP+ITfjdPekQH0+JfrhxbcuKDLXOzaBT/lRwzSzUWjvBNRYzkEGDYQITPmAsWEMAQtTHtPC0DDnQ20a3vXaGoEEHq6i2ijCEqX9PKqLwDS7kJ+ubYeT+F0zMDegvdMQIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url="http://scott.nat300.top/payNotify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url="http://scott.nat300.top/toTrade";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
