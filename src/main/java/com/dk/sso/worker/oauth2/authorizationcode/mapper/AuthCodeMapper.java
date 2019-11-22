package com.dk.sso.worker.oauth2.authorizationcode.mapper;

import com.dk.sso.worker.oauth2.authorizationcode.entity.AuthCode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthCodeMapper {
    void insertAuthCode(AuthCode authCode);

    AuthCode selectAuthCodeFromCode(String code);

    void deleteFromCode(String code);
}
