package com.atguigu.common.constant;

public class OrderConstant {

    public static  final  String USER_ORDER_TOKEN_PREFIX = "order:token:" ;

    /**
     * 订单的来源
     *
     * 0 ,表示来源于PC端
     * 1,表示来源于APP端
     */
    public static  enum OrderSourceType {

        PC(0),APP(1);

        Integer type ;

        OrderSourceType(Integer type){
            this.type = type ;
        }
        public Integer getType() {
            return type;
        }
    }

    /**
     * 订单的状态
     */
    public static  enum OrderStatus{
        CREATE_NEW(0,"待付款"),
        PAYED(1,"已付款"),
        SENDED(2,"已发货"),
        RECIEVED(3,"已完成"),
        CANCLED(4,"已取消"),
        SERVICING(5,"售后中"),
        SERVICED(6,"售后完成");
        private Integer code;
        private String msg;

        OrderStatus(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }


    }
}
