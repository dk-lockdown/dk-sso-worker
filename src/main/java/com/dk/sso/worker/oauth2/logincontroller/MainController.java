package com.dk.sso.worker.oauth2.logincontroller;

import com.dk.foundation.engine.baseentity.StandResponse;
import com.dk.foundation.engine.exception.BusinessException;
import com.dk.sso.worker.membership.application.entity.Application;
import com.dk.sso.worker.membership.application.service.ApplicationService;
import com.dk.sso.worker.membership.sms.conf.SmsConfig;
import com.dk.sso.worker.membership.sms.service.SmsService;
import com.dk.sso.worker.oauth2.OAuth2Authentication;
import com.dk.sso.worker.oauth2.OAuth2Request;
import com.dk.sso.worker.oauth2.approvalstore.servicewrap.ApprovalStore;
import com.dk.sso.worker.oauth2.approvalstore.entity.Approval;
import com.dk.sso.worker.oauth2.authorizationcode.servicewrap.AuthorizationCodeServices;
import com.dk.sso.worker.oauth2.clientdetails.entity.ClientDetails;
import com.dk.sso.worker.oauth2.clientdetails.service.ClientDetailsService;
import com.dk.sso.worker.oauth2.logincontroller.vo.LoginVO;
import com.dk.sso.worker.oauth2.logincontroller.vo.SmsLoginVO;
import com.dk.sso.worker.oauth2.userdetails.entity.UserDetails;
import com.dk.sso.worker.oauth2.userdetails.service.UserDetailsService;
import com.dk.sso.worker.util.OAuth2Utils;
import com.dk.startup.worker.util.BaseController;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

@Controller
public class MainController extends BaseController {

    @Resource
    private ApplicationService applicationService;

    @Resource
    private ClientDetailsService clientDetailsService;

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private ApprovalStore approvalStore;

    @Resource
    private AuthorizationCodeServices authorizationCodeServices;

    @Resource
    private SmsService smsService;

    @Resource
    private SmsConfig smsConfig;

    @Resource
    private PasswordEncoder encoder;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam("client_id") String clientId) {
        Application app = applicationService.selectApplicationByAppId(clientId);
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addAllObjects(new HashMap<String, String>(1) {
            {
                this.put("appName", app.getAppName());
            }
        });
        return modelAndView;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody
    StandResponse<String> login(@RequestBody LoginVO loginVO) throws BusinessException, UnsupportedEncodingException {
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(loginVO.getClientId());
        String grantType = "authorization_code";
        if (clientDetails.getAuthorizedGrantTypes() != null && !clientDetails.getAuthorizedGrantTypes().contains(grantType)) {
            throw new BusinessException("应用【" + clientDetails.getClientId() + "】不支持授权码模式登录");
        }

        String redirectUri = URLDecoder.decode(loginVO.getRedirectUri(), "UTF-8");
        if (clientDetails.getRegisteredRedirectUri() != null && !clientDetails.getRegisteredRedirectUri().contains(redirectUri)) {
            throw new BusinessException("重定向地址错误，请检查");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginVO.getUserName());
        if (userDetails == null || !encoder.matches(loginVO.getPassword(), userDetails.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        Calendar expiresAt = Calendar.getInstance();
        expiresAt.add(Calendar.MONTH, 1);
        List<Approval> approvalDataList = new ArrayList<>();
        approvalDataList.add(new Approval(loginVO.getUserName(), loginVO.getClientId(),
                loginVO.getScope(), expiresAt.getTime(), Approval.ApprovalStatus.APPROVED));
        approvalStore.addApprovals(approvalDataList);
        Map<String, String> requestParameters = new LinkedHashMap<>();
        requestParameters.put("response_type", loginVO.getResponseType());
        requestParameters.put("redirect_uri", loginVO.getRedirectUri());
        requestParameters.put("client_id", loginVO.getClientId());
        requestParameters.put("scope", loginVO.getScope());
        OAuth2Request oAuth2Request = new OAuth2Request(requestParameters,
                clientDetails.getClientId(),
                true,
                clientDetails.getScope(),
                clientDetails.getResourceIds(),
                loginVO.getRedirectUri(),
                OAuth2Utils.parseParameterList(loginVO.getResponseType()),
                new HashMap<String, Serializable>());
        OAuth2Authentication combinedAuth = new OAuth2Authentication(oAuth2Request, userDetails);
        String code = authorizationCodeServices.createAuthorizationCode(combinedAuth);
        return success(code);
    }

    @RequestMapping(value = "/smsLogin", method = RequestMethod.POST)
    public @ResponseBody
    StandResponse<String> smsLogin(@RequestBody SmsLoginVO loginVO) throws BusinessException, UnsupportedEncodingException {
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(loginVO.getClientId());
        String grantType = "sms";
        if (clientDetails.getAuthorizedGrantTypes() != null && !clientDetails.getAuthorizedGrantTypes().contains(grantType)) {
            throw new BusinessException("应用【" + clientDetails.getClientId() + "】不支持短信模式登录");
        }

        String redirectUri = URLDecoder.decode(loginVO.getRedirectUri(), "UTF-8");
        if (clientDetails.getRegisteredRedirectUri() != null && !clientDetails.getRegisteredRedirectUri().contains(redirectUri)) {
            throw new BusinessException("重定向地址错误，请检查");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginVO.getMobileNumber());
        if (userDetails == null) {
            throw new BusinessException("该手机号未注册");
        }

        Boolean successVerify = smsService.hasSmsVerifyCodeExpired(loginVO.getMobileNumber(), loginVO.getVerifyCode(), smsConfig.getKjdldxTemplate().getTemplateCode());

        if (!successVerify) {
            throw new BusinessException("短信验证码无效，请重新登录。");
        }
        smsService.updateSmsVerifyCodeIntoExpired(loginVO.getMobileNumber(), smsConfig.getKjdldxTemplate().getTemplateCode());

        Calendar expiresAt = Calendar.getInstance();
        expiresAt.add(Calendar.MONTH, 1);
        List<Approval> approvalDataList = new ArrayList<>();
        approvalDataList.add(new Approval(userDetails.getUsername(), loginVO.getClientId(),
                loginVO.getScope(), expiresAt.getTime(), Approval.ApprovalStatus.APPROVED));
        approvalStore.addApprovals(approvalDataList);
        Map<String, String> requestParameters = new LinkedHashMap<>();
        requestParameters.put("response_type", loginVO.getResponseType());
        requestParameters.put("redirect_uri", loginVO.getRedirectUri());
        requestParameters.put("client_id", loginVO.getClientId());
        requestParameters.put("scope", loginVO.getScope());
        OAuth2Request oAuth2Request = new OAuth2Request(requestParameters,
                clientDetails.getClientId(),
                true,
                clientDetails.getScope(),
                clientDetails.getResourceIds(),
                loginVO.getRedirectUri(),
                OAuth2Utils.parseParameterList(loginVO.getResponseType()),
                new HashMap<String, Serializable>());
        OAuth2Authentication combinedAuth = new OAuth2Authentication(oAuth2Request, userDetails);
        String code = authorizationCodeServices.createAuthorizationCode(combinedAuth);
        return success(code);
    }
}
