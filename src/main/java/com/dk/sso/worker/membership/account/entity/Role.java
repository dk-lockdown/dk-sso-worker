package com.dk.sso.worker.membership.account.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Role implements Serializable {

    private Long roleSysNo;

    private String loweredRoleName;

    private String roleName;

    private String description;

    private Long appSysNo;

}
