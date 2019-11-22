package com.dk.sso.worker.membership.application.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.dk.foundation.engine.baseentity.PageResult;
import com.dk.foundation.engine.exception.BusinessException;
import com.dk.sso.worker.membership.application.entity.Application;
import com.dk.sso.worker.membership.application.mapper.ApplicationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ApplicationService {
    private PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Resource
    ApplicationMapper applicationMapper;

    public Application selectApplicationByAppId(String appId) {
        return applicationMapper.selectApplicationByAppId(appId);
    }

    public void insertApplication(Application application) throws BusinessException {
        if(applicationMapper.ifApplicationExistsByAppId(application.getAppId()))
        {
            throw new BusinessException("AppId已经存在");
        }
        if(applicationMapper.ifApplicationExistsByAppName(application.getAppName()))
        {
            throw new BusinessException("AppName已经存在");
        }
        application.setAppSecretEncrypted(encoder.encode(application.getAppSecret()));
        applicationMapper.insertApplication(application);
    }

    public PageResult<Application> selectApplicationsByPager(String appId, Integer pageIndex, Integer pageSize)
    {
        PageHelper.startPage(pageIndex, pageSize);
        List<Application> apps = applicationMapper.selectApplicationsByPager(appId);
        PageInfo<Application> pageInfo = new PageInfo<>(apps);
        PageResult<Application> result = new PageResult<>(apps,pageInfo.getTotal(),pageIndex,pageSize);
        return result;
    }
}
