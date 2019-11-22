package com.dk.sso.worker.membership.account.controller.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateNickNameRequestVO {
    @NotNull(message = "昵称不能为空")
    private String nickname;
}
