package com.dk.sso.worker.oauth2.logincontroller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SmsLoginVO {
    @ApiModelProperty(value = "手机号")
    private String mobileNumber;

    @ApiModelProperty(value = "验证码")
    private String verifyCode;

    @ApiModelProperty(value = "客户端编号")
    private String clientId;

    @ApiModelProperty(value = "返回类型")
    private String responseType;

    @ApiModelProperty(value = "重定向地址")
    private String redirectUri;

    @ApiModelProperty(value = "scope")
    private String scope;
}
