package com.atguigu.gulimall.auth.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AccessTokenResponseDto {

    private String access_token ;
    private String expires_in ;
    private String remind_in ;
    private String uid ;
}
