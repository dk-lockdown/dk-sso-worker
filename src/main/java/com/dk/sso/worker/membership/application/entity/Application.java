package com.dk.sso.worker.membership.application.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Application {
    @ApiModelProperty(value = "应用ID")
    private String appId;
    @ApiModelProperty(value = "应用名称")
    private String appName;
    @ApiModelProperty(value = "加密的应用密码")
    @JsonIgnore
    private String appSecretEncrypted;
    @ApiModelProperty(value = "应用密码")
    private String appSecret;
    /**
     * 授权类型，用英文逗号分隔，取值只能是password、authorization_code
     */
    @ApiModelProperty(value = "授权类型")
    @JsonIgnore
    private String grantTypes;
    @ApiModelProperty(value = "重定向地址")
    @JsonIgnore
    private String redirectUrl;
    @ApiModelProperty(value = "过期时间")
    @JsonIgnore
    private Integer expireIn;
    private String description;
}
