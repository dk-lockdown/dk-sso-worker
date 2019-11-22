package com.dk.sso.worker.oauth2.authorizationcode.servicewrap;

import com.dk.foundation.engine.exception.BusinessException;
import com.dk.sso.worker.oauth2.OAuth2Authentication;

public interface AuthorizationCodeServices {

    String createAuthorizationCode(OAuth2Authentication authentication);


    OAuth2Authentication consumeAuthorizationCode(String code) throws BusinessException;
}
