package com.atguigu.gulimall.product.exception;

public class PhoneRepeatException extends RuntimeException {
    public PhoneRepeatException(){
        super("手机号码重复");
    }
}
