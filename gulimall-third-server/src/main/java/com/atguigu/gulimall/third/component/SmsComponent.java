package com.atguigu.gulimall.third.component;

import com.atguigu.gulimall.third.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SmsComponent {

   /* @Value("${sms.post}")
   private String hosts ;
    @Value("${sms.path}")
   private String paths ;
    @Value("${sms.appcode}")
   private String appcodes ;
*/

    /**
     * 发送短信验证码
     * @param phone
     * @param code
     */
    public void sendSmsCode(String phone,String code){
        String host = "http://smsmsgs.market.alicloudapi.com" ;
        String path = "/smsmsgs";
        String method = "GET";
        String appcode ="70f34f1e53544c758409da40d9ad88e8" ;
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("param", code);
        querys.put("phone", phone);
        querys.put("sign", "175622");
        querys.put("skin", "20");
        //JDK 1.8示例代码请在这里下载：  http://code.fegine.com/Tools.zip

        try {

            HttpResponse response= HttpUtils.doGet(host, path, method, headers, querys);
            //System.out.println(response.toString());如不输出json, 请打开这行代码，打印调试头部状态码。
            //状态码: 200 正常；400 URL无效；401 appCode错误； 403 次数用完； 500 API网管错误
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
