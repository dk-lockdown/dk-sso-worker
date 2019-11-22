package com.dk.sso.worker.oauth2.logincontroller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginVO {
    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "客户端编号")
    private String clientId;

    @ApiModelProperty(value = "返回类型")
    private String responseType;

    @ApiModelProperty(value = "重定向地址")
    private String redirectUri;

    @ApiModelProperty(value = "scope")
    private String scope;
}
