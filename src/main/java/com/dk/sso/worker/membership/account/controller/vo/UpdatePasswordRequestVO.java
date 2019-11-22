package com.dk.sso.worker.membership.account.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdatePasswordRequestVO {
    @NotNull(message = "密码不能为空")
    @ApiModelProperty(value = "旧密码", required = true)
    private String passWord;

    @NotNull(message = "新密码不能为空")
    @ApiModelProperty(value = "新密码", required = true)
    private String newPassWord;
}
