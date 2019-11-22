package com.dk.sso.worker.oauth2.approvalstore.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ApprovalData implements Serializable {

    private String userId;

    private String clientId;

    private String scope;

    private String status;

    private Date expiresAt;

    private Date lastUpdatedAt;
}
