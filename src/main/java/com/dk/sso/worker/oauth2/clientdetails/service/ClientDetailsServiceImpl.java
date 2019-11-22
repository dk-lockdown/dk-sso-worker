package com.dk.sso.worker.oauth2.clientdetails.service;

import com.dk.foundation.engine.exception.BusinessException;
import com.dk.sso.worker.membership.application.entity.Application;
import com.dk.sso.worker.membership.application.service.ApplicationService;
import com.dk.sso.worker.oauth2.clientdetails.entity.ClientDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientDetailsServiceImpl implements ClientDetailsService {
    @Autowired
    ApplicationService applicationService;

    @Override
    public ClientDetails loadClientByClientId(String s) throws BusinessException {
        if(StringUtils.isBlank(s))
        {
            throw new BusinessException("AppId不能为空");
        }
        Application application = applicationService.selectApplicationByAppId(s);
        return new ClientDetails(application);
    }
}
