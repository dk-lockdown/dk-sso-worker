package com.dk.sso.worker.membership.sms.mapper;

import com.dk.sso.worker.membership.sms.entity.SmsVerifyCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SmsVerifyCodeMapper {

    void insertSmsVerifyCode(@Param("mobileNumber") String mobileNumber, @Param("verifyCode") String verifyCode,
                             @Param("expireIn") int expireIn, @Param("retryDuration") int retryDuration,
                             @Param("templateKey") String templateKey);

    void updateSmsVerifyCodeIntoExpired(@Param("mobileNumber") String mobileNumber, @Param("templateKey") String templateKey);

    List<SmsVerifyCode> selectALLSmsVerifyCodeToday(@Param("mobileNumber") String mobileNumber, @Param("templateKey") String templateKey);

    Boolean hasSmsVerifyCodeExpired(@Param("mobileNumber") String mobileNumber, @Param("verifyCode") String verifyCode, @Param("templateKey") String templateKey);

    int updateSmsVerifyFailCount(@Param("mobileNumber") String mobileNumber, @Param("templateKey") String templateKey);
}
