package com.dk.sso.worker.membership.application.mapper;

import com.dk.sso.worker.membership.application.entity.Application;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApplicationMapper {
    void insertApplication(Application application);

    Boolean ifApplicationExistsByAppName(String appName);

    Boolean ifApplicationExistsByAppId(String appId);

    Application selectApplicationByAppId(String appId);

    List<Application> selectApplicationsByPager(@Param("appId") String appId);
}
