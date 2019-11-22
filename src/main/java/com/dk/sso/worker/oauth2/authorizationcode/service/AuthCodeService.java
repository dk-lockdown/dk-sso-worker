package com.dk.sso.worker.oauth2.authorizationcode.service;

import com.dk.sso.worker.oauth2.authorizationcode.entity.AuthCode;
import com.dk.sso.worker.oauth2.authorizationcode.mapper.AuthCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthCodeService {
    @Autowired
    private AuthCodeMapper authCodeMapper;

    public void store(AuthCode authCode) {
        authCodeMapper.insertAuthCode(authCode);
    }

    public byte[] remove(String code) {
        AuthCode authCode = authCodeMapper.selectAuthCodeFromCode(code);
        if (authCode != null) {
            authCodeMapper.deleteFromCode(code);
            return authCode.getAuthentication();
        }
        return null;
    }

}
