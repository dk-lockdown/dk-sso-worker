package com.dk.sso.worker.membership.account.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class User {

    @ApiModelProperty(value = "用户ID，主键")
    private Long userId;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "昵称")
    private String nickName;
    @ApiModelProperty(value = "密码")
    @JsonIgnore
    private String password;
    /**
     * 1:Bcrypt 2:MD5
     */
    @ApiModelProperty(value = "密码加密方式")
    @JsonIgnore
    private Integer passwordFormat;
    @ApiModelProperty(value = "密码加盐")
    @JsonIgnore
    private String passwordSalt;
    @ApiModelProperty(value = "手机号")
    private String mobileNumber;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "邮箱小写")
    private String loweredEmail;
    @ApiModelProperty(value = "头像")
    private String avatar;
    private Boolean isApproved;
    private String comment;
    private String appId;
    private Date lastPasswordChangedDate;
    @JsonIgnore
    private List<Role> roleList;
}
