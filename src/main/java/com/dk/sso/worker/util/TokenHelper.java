package com.dk.sso.worker.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.dk.sso.worker.constant.JWTConstant;
import com.dk.sso.worker.oauth2.clientdetails.entity.ClientDetails;
import com.dk.sso.worker.oauth2.clientdetails.service.ClientDetailsService;
import com.dk.startup.worker.interceptor.entity.TokenExtractResult;

import java.util.Base64;

public class TokenHelper {
    public static TokenExtractResult extract(String token, ClientDetailsService clientDetailsService){
        String t = token.substring(OAuth2Utils.BEARER_TYPE.length()+1);
        String[] st = t.split("\\.",3);
        if(st==null||st.length!=3) {
            return null;
        }
        Base64.Decoder decoder = Base64.getDecoder();
        TokenExtractResult tokenExtractResult = new TokenExtractResult();
        try {
            String claims = new String(decoder.decode(st[1]), "UTF-8");
            JSONObject tokenObj = JSON.parseObject(claims);
            String clientId = tokenObj.getString(JWTConstant.CLAIM_CLIENT_ID);
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
            Algorithm algorithm = Algorithm.HMAC256(clientDetails.getClientSecret());
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(t);
            tokenExtractResult.setCode(1);
            tokenExtractResult.setUserId(tokenObj.getLong(JWTConstant.CLAIM_USER_ID));
            tokenExtractResult.setUserName(tokenObj.getString(JWTConstant.CLAIM_USER_NAME));
            return tokenExtractResult;
        } catch (TokenExpiredException e){
            e.printStackTrace();
            tokenExtractResult.setCode(0);
            return tokenExtractResult;
        } catch (Exception ex){
            ex.printStackTrace();
            tokenExtractResult.setCode(-1);
            return tokenExtractResult;
        }
    }
}
