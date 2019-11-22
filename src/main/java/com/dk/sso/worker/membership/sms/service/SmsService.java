package com.dk.sso.worker.membership.sms.service;

import com.dk.foundation.engine.exception.BusinessException;
import com.dk.sso.worker.membership.sms.conf.SmsConfig;
import com.dk.sso.worker.membership.sms.entity.SmsVerifyCode;
import com.dk.sso.worker.membership.sms.mapper.SmsVerifyCodeMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SmsService {

    private static Logger log = LoggerFactory.getLogger(SmsService.class);

    @Autowired
    private SmsConfig smsConfig;

    @Resource
    private SmsVerifyCodeMapper smsVerifyCodeMapper;


    /**
     * 发送快捷登录短信验证码
     *
     * @param mobile
     * @return
     */
    @Transactional(rollbackFor = Throwable.class)
    public Boolean sendLoginVerifyCode(String mobile) throws BusinessException {
        checkBeforeSendSms(mobile, smsConfig.getKjdldxTemplate().getTemplateCode());
        String verifyCode = getRandomSmsCode();
        boolean sendFlag = sendSms(smsConfig.getKjdldxTemplate().getTemplateCode(),mobile, verifyCode);
        if (!sendFlag) {
            return false;
        }
        smsVerifyCodeMapper.updateSmsVerifyCodeIntoExpired(mobile, smsConfig.getKjdldxTemplate().getTemplateCode());
        smsVerifyCodeMapper.insertSmsVerifyCode(mobile, verifyCode,
                smsConfig.getKjdldxTemplate().getExpireIn(),
                smsConfig.getKjdldxTemplate().getRetryDuration(),
                smsConfig.getKjdldxTemplate().getTemplateCode());
        return true;
    }


    /**
     * 验证短信
     *
     * @param mobile
     * @param verifyCode
     */
    public Boolean hasSmsVerifyCodeExpired(String mobile, String verifyCode, String templateKey) throws BusinessException {
        if (!isMobiles(mobile)) {
            throw new BusinessException("手机号非法，请检查。");
        }
        if (StringUtils.isEmpty(verifyCode)) {
            throw new BusinessException("短信验证码不能为空，请检查。");
        }
        Boolean result = smsVerifyCodeMapper.hasSmsVerifyCodeExpired(mobile, verifyCode, templateKey);
        if(!result){
            int rowCount = smsVerifyCodeMapper.updateSmsVerifyFailCount(mobile, templateKey);
            if(rowCount==0){
                smsVerifyCodeMapper.updateSmsVerifyCodeIntoExpired(mobile, templateKey);
            }
        }
        return result;
    }

    public void updateSmsVerifyCodeIntoExpired(String mobileNumber, String templateKey){
        smsVerifyCodeMapper.updateSmsVerifyCodeIntoExpired(mobileNumber, templateKey);
    }

    /**
     * 获取随机短信码
     *
     * @return
     */
    private String getRandomSmsCode() {
        String verifyCode = String
                .valueOf(new Random().nextInt(899999) + 100000);
        return verifyCode;
    }

    /**
     * 发送短信前检测
     */
    private void checkBeforeSendSms(String mobile, String templateKey) throws BusinessException {
        if (!isMobiles(mobile)) {
            throw new BusinessException("手机号非法，请检查。");
        }

        //判断发送短信间隔是否为配置时间间隔内
        //判断是否发送超过配置限制的条数
        List<SmsVerifyCode> codeList = smsVerifyCodeMapper.selectALLSmsVerifyCodeToday(mobile, templateKey);
        if (!CollectionUtils.isEmpty(codeList)) {
            SmsVerifyCode smsVerifyCode = codeList.get(0);
            if (smsVerifyCodeMapper.hasSmsVerifyCodeExpired(mobile, smsVerifyCode.getVerifyCode(), templateKey)) {
                throw new BusinessException("验证码请求太频繁，请检查。");
            }
            if (codeList.size() >= smsConfig.getTotalTimesEachDay()) {
                throw new BusinessException("当日验证码请求数已经超过最大限制，请明天再试。");
            }
        }
    }

    /**
     * 验证是否满足手机号格式
     *
     * @param str
     * @return
     */
    public static boolean isMobiles(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        String check = "^1\\d{10}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(str.trim());
        return matcher.matches();
    }

    /**
     * 发送短信
     *
     * @param mobile
     * @param smsCode
     * @return
     */
    private boolean sendSms(String templateCode,String mobile, String smsCode) {
        /*
         * todo
         */
        return false;
    }
}