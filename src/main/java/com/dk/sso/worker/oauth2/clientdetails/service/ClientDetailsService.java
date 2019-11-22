package com.dk.sso.worker.oauth2.clientdetails.service;

import com.dk.foundation.engine.exception.BusinessException;
import com.dk.sso.worker.oauth2.clientdetails.entity.ClientDetails;

public interface ClientDetailsService {
    ClientDetails loadClientByClientId(String clientId) throws BusinessException;
}
