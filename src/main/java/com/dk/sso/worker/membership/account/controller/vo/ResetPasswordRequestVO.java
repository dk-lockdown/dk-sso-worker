package com.dk.sso.worker.membership.account.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author scottlewis
 */
@Data
public class ResetPasswordRequestVO {
    @NotNull(message = "手机号")
    @ApiModelProperty(value = "手机号", required = true)
    private String mobileNumber;

    @NotNull(message = "验证码")
    @ApiModelProperty(value = "验证码", required = true)
    private String verifyCode;

    @NotNull(message = "新密码不能为空")
    @ApiModelProperty(value = "新密码", required = true)
    private String newPassWord;
}
