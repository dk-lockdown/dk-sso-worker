package com.dk.sso.worker.membership.sms.controller;

import com.dk.foundation.engine.baseentity.StandResponse;
import com.dk.foundation.engine.exception.BusinessException;
import com.dk.sso.worker.membership.sms.conf.SmsConfig;
import com.dk.sso.worker.membership.sms.service.SmsService;
import com.dk.startup.worker.util.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/v1/sms")
@Api(value = "短信", produces = "application/json;charset=UTF-8")
@Validated
public class SmsController extends BaseController {

    @Autowired
    private SmsService smsService;

    @Autowired
    private SmsConfig smsConfig;


    @ApiOperation(value = "发送快捷登录验证码", notes = "")
    @RequestMapping(value = "/sendLoginSmsVerifyCode", method = RequestMethod.POST)
    public @ResponseBody
    StandResponse<Boolean> sendLoginVerifyCode(String mobile) throws BusinessException {
        return success(smsService.sendLoginVerifyCode(mobile));
    }


    @ApiOperation(value = "验证快捷登录短信验证码是否过期", notes = "")
    @RequestMapping(value = "/hasLoginSmsVerifyCodeExpired", method = RequestMethod.POST)
    public @ResponseBody
    StandResponse<Boolean> hasLoginSmsVerifyCodeExpired(String mobile, String verifyCode) throws BusinessException {
        return success(smsService.hasSmsVerifyCodeExpired(mobile, verifyCode, smsConfig.getKjdldxTemplate().getTemplateCode()));
    }

}