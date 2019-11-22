package com.dk.sso.worker.membership.sms.entity;

import lombok.Data;

import java.util.Date;

@Data
public class SmsVerifyCode {

    private Long id;

    private String mobileNumber;

    private String verifyCode;

    private String templateKey;

    private Date expireIn;

    private Date nextRetry;

    private Date createDate;
}
