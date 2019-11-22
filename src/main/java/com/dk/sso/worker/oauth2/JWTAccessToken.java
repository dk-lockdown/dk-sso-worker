package com.dk.sso.worker.oauth2;

import com.alibaba.fastjson.annotation.JSONField;
import com.dk.sso.worker.util.OAuth2Utils;

import java.util.Date;

public class JWTAccessToken {
    /**
     * token id
     */
    private String jti;

    @JSONField(serialize=false)
    private Long userId;

    @JSONField(serialize=false)
    private String userName;

    @JSONField(serialize=false)
    private String clientId;

    @JSONField(serialize=false)
    private Date expiration;

    @JSONField(serialize=false)
    private Date refreshTokenExpiration;

    @JSONField(name = "access_token")
    private String accessToken;

    @JSONField(name = "refresh_token")
    private String refreshToken;

    @JSONField(serialize=false)
    private Boolean needRefreshToken = false;

    @JSONField(name = "token_type")
    private String tokenType = OAuth2Utils.BEARER_TYPE;

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }


    public Date getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public void setRefreshTokenExpiration(Date refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public Boolean getNeedRefreshToken() {
        return needRefreshToken;
    }

    public void setNeedRefreshToken(Boolean needRefreshToken) {
        this.needRefreshToken = needRefreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public int getExpires_in() {
        return expiration != null ? Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L)
                .intValue() : 0;
    }

    public void setExpires_in(int delta) {
        setExpiration(new Date(System.currentTimeMillis() + delta));
    }
}
