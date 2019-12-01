package com.dk.sso.worker.oauth2.logincontroller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginVO {
    @ApiModelProperty(value = "用户名")
    @NotNull(message = "用户名不能为空")
    private String userName;

    @ApiModelProperty(value = "密码")
    @NotNull(message = "密码不能为空")
    private String password;

    @ApiModelProperty(value = "客户端编号")
    @NotNull(message = "客户端编号不能为空")
    private String clientId;

    @ApiModelProperty(value = "返回类型")
    @NotNull(message = "返回类型不能为空")
    private String responseType;

    @ApiModelProperty(value = "重定向地址")
    @NotNull(message = "重定向地址不能为空")
    private String redirectUri;

    @ApiModelProperty(value = "scope")
    @NotNull(message = "scope不能为空")
    private String scope;

    @ApiModelProperty(value = "state")
    private String state;
}
