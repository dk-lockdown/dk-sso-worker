package com.dk.sso.worker.oauth2.authorizationcode.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthCode implements Serializable {

    private String code;

    private byte[] authentication;
}
