package com.dk.sso.worker.oauth2.clientdetails.entity;

import com.dk.sso.worker.membership.application.entity.Application;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class ClientDetails {
    private static final long serialVersionUID = 1L;

    private String appId;
    private String appSecret;
    private String grantTypes;
    private String redirectUrl;
    private Integer expireIn;


    public ClientDetails(Application application){
        this.appId=application.getAppId();
        this.appSecret=application.getAppSecret();
        this.grantTypes=application.getGrantTypes();
        this.redirectUrl=application.getRedirectUrl();
        this.expireIn =application.getExpireIn();
    }

    public String getClientId() {
        return appId;
    }

    public Set<String> getResourceIds() {
        return null;
    }

    public boolean isSecretRequired() {
        return true;
    }

    public String getClientSecret() {
        return appSecret;
    }

    public boolean isScoped() {
        return true;
    }

    public Set<String> getScope() {
        return new HashSet<>(Arrays.asList("all"));
    }

    public Set<String> getAuthorizedGrantTypes() {
        if(StringUtils.isNotBlank(grantTypes)) {
            String[] gtArray = grantTypes.split(",");
            if (gtArray != null && gtArray.length > 0) {
                Set<String> gts = new HashSet<String>(Arrays.asList(gtArray));
                return gts;
            }
        }
        return null;
    }

    public Set<String> getRegisteredRedirectUri() {
        if(StringUtils.isNotBlank(redirectUrl)) {
            Set<String> redirectUris = new HashSet<String>(1);
            redirectUris.add(redirectUrl);
            return redirectUris;
        }
        return null;
    }

    public Collection<String> getAuthorities() {
        return new ArrayList<>();
    }

    public Integer getAccessTokenValiditySeconds() {
        return expireIn;
    }

    public Integer getRefreshTokenValiditySeconds() {
        return expireIn*5;
    }

    public boolean isAutoApprove(String s) {
        return true;
    }

    public Map<String, Object> getAdditionalInformation() {
        return null;
    }
}
