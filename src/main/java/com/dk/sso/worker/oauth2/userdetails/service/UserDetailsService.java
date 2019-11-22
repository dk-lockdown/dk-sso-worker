package com.dk.sso.worker.oauth2.userdetails.service;

import com.dk.foundation.engine.exception.BusinessException;
import com.dk.sso.worker.oauth2.userdetails.entity.UserDetails;

public interface UserDetailsService {
    UserDetails loadUserByUsername(String username) throws BusinessException;
}
