package com.dk.sso.worker.membership.interceptor.service;

import com.dk.sso.worker.oauth2.clientdetails.service.ClientDetailsService;
import com.dk.sso.worker.util.TokenHelper;
import com.dk.startup.worker.interceptor.entity.TokenExtractResult;
import com.dk.startup.worker.interceptor.req.OperationLogReq;
import com.dk.startup.worker.interceptor.service.AccountSvc;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AccountSvcImpl implements AccountSvc {
    @Resource
    ClientDetailsService clientDetailsService;

    @Override
    public TokenExtractResult tokenExtract(String token) {
        return TokenHelper.extract(token,clientDetailsService);
    }

    @Override
    public Boolean ifUserHasRole(Long aLong, String s) {
        return null;
    }

    @Override
    public Boolean ifUserHasPermission(Long aLong, String s) {
        return null;
    }

    @Override
    public Long createOperationLog(OperationLogReq operationLogReq) {
        return null;
    }

    @Override
    public Boolean signatureVerify(String s, String s1, String s2) {
        return null;
    }
}
