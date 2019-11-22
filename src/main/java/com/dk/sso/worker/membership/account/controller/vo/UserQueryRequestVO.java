package com.dk.sso.worker.membership.account.controller.vo;

import com.dk.foundation.engine.baseentity.Pager;
import lombok.Data;

@Data
public class UserQueryRequestVO extends Pager {
    private String userName;
}
