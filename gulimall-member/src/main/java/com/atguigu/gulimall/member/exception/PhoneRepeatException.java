package com.atguigu.gulimall.member.exception;

public class PhoneRepeatException extends RuntimeException {
    public PhoneRepeatException(){
        super("手机号码重复");
    }
}
