package com.dk.sso.worker.membership.account.controller.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateAvatarRequestVO {
    @NotNull(message = "头像不能为空")
    private String avatar;
}
