package com.dk.sso.worker.membership.account.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserRegisterVO {
    @NotNull(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名", required = true)
    private String userName;

    @NotNull(message = "密码不能为空")
    @ApiModelProperty(value = "密码", required = true)
    private String passWord;

    @NotNull(message = "appId不能为空")
    @ApiModelProperty(value = "appId", required = true)
    private String appId;

    @NotBlank(message = "appSecret不能为空")
    @ApiModelProperty(value = "appSecret", required = true)
    private String appSecret;
}
