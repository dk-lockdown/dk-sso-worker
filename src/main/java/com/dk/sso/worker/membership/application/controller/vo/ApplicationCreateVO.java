package com.dk.sso.worker.membership.application.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhanghonghao
 * Description : 新增应用VO
 * Created on 2018/11/20 上午11:26
 */
@Data
public class ApplicationCreateVO {

    @NotNull(message = "appId不能为空")
    @ApiModelProperty(value = "appId", required = true)
    private String appId;

    @NotNull(message = "appName不能为空")
    @ApiModelProperty(value = "appName", required = true)
    private String appName;

    @NotNull(message = "授权模式不能为空")
    @ApiModelProperty(value = "grantType", allowableValues = "password,authorization_code")
    private String grantType;

    @ApiModelProperty(value = "expireIn", notes = "过期时间，默认为64000")
    private Integer expireIn;
}
