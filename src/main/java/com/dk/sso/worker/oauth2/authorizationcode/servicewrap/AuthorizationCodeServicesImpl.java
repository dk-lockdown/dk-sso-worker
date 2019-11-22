package com.dk.sso.worker.oauth2.authorizationcode.servicewrap;

import com.dk.foundation.common.RandomValueStringGenerator;
import com.dk.foundation.engine.exception.BusinessException;
import com.dk.sso.worker.oauth2.OAuth2Authentication;
import com.dk.sso.worker.oauth2.authorizationcode.entity.AuthCode;
import com.dk.sso.worker.oauth2.authorizationcode.service.AuthCodeService;
import com.dk.sso.worker.util.SerializationUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("authorizationCodeService")
public class AuthorizationCodeServicesImpl implements AuthorizationCodeServices {

    private RandomValueStringGenerator generator = new RandomValueStringGenerator();

    @Resource
    private AuthCodeService authCodeService;

    private void store(String code, OAuth2Authentication authentication) {
        AuthCode authCode = new AuthCode();
        authCode.setCode(code);
        authCode.setAuthentication(SerializationUtils.serialize(authentication));
        authCodeService.store(authCode);
    }

    private OAuth2Authentication remove(String code) {
        byte[] authCodeBytes = authCodeService.remove(code);
        if (authCodeBytes != null) {
            return SerializationUtils.deserialize(authCodeBytes);
        }
        return null;
    }

    @Override
    public String createAuthorizationCode(OAuth2Authentication authentication) {
        String code = generator.generate();
        store(code, authentication);
        return code;
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code) throws BusinessException {
        OAuth2Authentication auth = this.remove(code);
        if (auth == null) {
            throw new BusinessException("Invalid authorization code: " + code);
        }
        return auth;
    }
}
