package com.dk.sso.worker.oauth2;

import com.dk.sso.worker.oauth2.userdetails.entity.UserDetails;

import java.io.Serializable;

public class OAuth2Authentication implements Serializable {
    private static final long serialVersionUID = -4809832298438307309L;

    private final OAuth2Request storedRequest;

    private final UserDetails userDetails;

    public OAuth2Authentication(OAuth2Request oAuth2Request,UserDetails userDetails) {
        this.storedRequest = oAuth2Request;
        this.userDetails = userDetails;
    }


    public OAuth2Request getStoredRequest() {
        return storedRequest;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public boolean isAuthenticated() {
        return this.storedRequest.isApproved();
    }
}
