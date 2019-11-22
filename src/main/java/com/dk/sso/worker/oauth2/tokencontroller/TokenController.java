package com.dk.sso.worker.oauth2.tokencontroller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.dk.foundation.engine.baseentity.StandResponse;
import com.dk.foundation.engine.exception.BusinessException;
import com.dk.sso.worker.constant.JWTConstant;
import com.dk.sso.worker.membership.sms.conf.SmsConfig;
import com.dk.sso.worker.membership.sms.service.SmsService;
import com.dk.sso.worker.oauth2.JWTAccessToken;
import com.dk.sso.worker.oauth2.OAuth2Authentication;
import com.dk.sso.worker.oauth2.OAuth2Request;
import com.dk.sso.worker.oauth2.authorizationcode.servicewrap.AuthorizationCodeServices;
import com.dk.sso.worker.oauth2.clientdetails.entity.ClientDetails;
import com.dk.sso.worker.oauth2.clientdetails.service.ClientDetailsService;
import com.dk.sso.worker.oauth2.userdetails.entity.UserDetails;
import com.dk.sso.worker.oauth2.userdetails.service.UserDetailsService;
import com.dk.sso.worker.util.OAuth2Utils;
import com.dk.sso.worker.util.TokenHelper;
import com.dk.startup.worker.interceptor.entity.TokenExtractResult;
import com.dk.startup.worker.util.BaseController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.*;

@Controller
public class TokenController extends BaseController {
    /**
     * default 30 days.
     */
    private int refreshTokenValiditySeconds = 60 * 60 * 24 * 30;

    /**
     * default 12 hours.
     */
    private int accessTokenValiditySeconds = 60 * 60 * 12;

    @Resource
    private ClientDetailsService clientDetailsService;

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private AuthorizationCodeServices authorizationCodeServices;

    @Resource
    private SmsService smsService;

    @Resource
    private SmsConfig smsConfig;

    @Resource
    private PasswordEncoder encoder;

    @RequestMapping(value = "/oauth/token", method=RequestMethod.GET)
    public ResponseEntity<JWTAccessToken> getAccessToken(@RequestParam
            Map<String, String> parameters) throws BusinessException {
        return postAccessToken(parameters);
    }

    @RequestMapping(value = "/oauth/token", method=RequestMethod.POST)
    public ResponseEntity<JWTAccessToken> postAccessToken(@RequestParam
            Map<String, String> parameters) throws BusinessException {

        String grantType = parameters.get(OAuth2Utils.GRANT_TYPE);
        String clientId = parameters.get(OAuth2Utils.CLIENT_ID);
        String clientSecret = parameters.get(JWTConstant.PARA_CLIENT_SECRET);
        String scope = parameters.get(OAuth2Utils.SCOPE);

        if (!StringUtils.hasText(grantType)) {
            throw new BusinessException("缺少 grant_type");
        }
        if (grantType.equals(JWTConstant.GT_IMPLICIT)) {
            throw new BusinessException("Implicit 模式不支持");
        }
        if (!StringUtils.hasText(clientId)) {
            throw new BusinessException("缺少 client_id");
        }
        if (!StringUtils.hasText(clientSecret)) {
            throw new BusinessException("缺少 client_secret");
        }
        if (!StringUtils.hasText(clientSecret)) {
            throw new BusinessException("缺少 scope");
        }

        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        if (clientDetails != null) {
            validateScope(new HashSet<>(Arrays.asList(scope)), clientDetails.getScope());
        }
        validateGrantType(grantType, clientDetails);
        if(!clientDetails.getClientSecret().equals(clientSecret)) {
            throw new BusinessException("client secret 不匹配");
        }

        if(isAuthCodeRequest(parameters) || isSmsCodeRequest(parameters)){
            return getResponse(authorizationCodeTokenGranter(parameters, clientDetails));
        }
        if(isPasswordRequest(parameters)) {
            return getResponse(passwordTokenGranter(parameters, clientDetails));
        }
        if(isSmsRequest(parameters)){
            return getResponse(smsTokenGranter(parameters, clientDetails));
        }
        if(isRefreshTokenRequest(parameters)){
            return getResponse(refreshTokenGranter(parameters,clientDetails));
        }

        throw new BusinessException("不支持的认证方式");
    }

    private void validateScope(Set<String> requestScopes, Set<String> clientScopes) throws BusinessException {
        if (clientScopes != null && !clientScopes.isEmpty()) {
            for (String scope : requestScopes) {
                if (!clientScopes.contains(scope)) {
                    throw new BusinessException("非法的：" + scope);
                }
            }
        }

        if (requestScopes.isEmpty()) {
            throw new BusinessException("scope 不能为空");
        }
    }

    protected void validateGrantType(String grantType, ClientDetails clientDetails) throws BusinessException {
        Collection<String> authorizedGrantTypes = clientDetails.getAuthorizedGrantTypes();
        if (authorizedGrantTypes != null && !authorizedGrantTypes.isEmpty()
                && !authorizedGrantTypes.contains(grantType)) {
            throw new BusinessException("不支持的认证方式：" + grantType);
        }
    }


    private ResponseEntity<JWTAccessToken> getResponse(JWTAccessToken accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        return new ResponseEntity<JWTAccessToken>(accessToken, headers, HttpStatus.OK);
    }

    private boolean isAuthCodeRequest(Map<String, String> parameters) {
        return JWTConstant.GT_AUTHORIZATION_CODE.equals(parameters.get(OAuth2Utils.GRANT_TYPE))
                && parameters.get("code") != null;
    }

    private boolean isSmsCodeRequest(Map<String, String> parameters) {
        return JWTConstant.GT_SMS.equals(parameters.get(OAuth2Utils.GRANT_TYPE))
                && parameters.get("code") != null;
    }

    private boolean isPasswordRequest(Map<String, String> parameters) {
        return JWTConstant.GT_PASSWORD.equals(parameters.get(OAuth2Utils.GRANT_TYPE))
                && parameters.get(JWTConstant.PARA_USERNAME) != null
                && parameters.get(JWTConstant.PARA_PASSWORD) != null;
    }

    private boolean isSmsRequest(Map<String, String> parameters) {
        return JWTConstant.GT_SMS.equals(parameters.get(OAuth2Utils.GRANT_TYPE))
                && parameters.get(JWTConstant.PARA_MOBILE_NUMBER) != null
                && parameters.get(JWTConstant.PARA_VERIFY_CODE) != null;
    }

    private boolean isRefreshTokenRequest(Map<String, String> parameters) {
        return JWTConstant.GT_REFRESH_TOKEN.equals(parameters.get(OAuth2Utils.GRANT_TYPE))
                && parameters.get(JWTConstant.GT_REFRESH_TOKEN) != null;
    }

    /**
     * 授权码登录获取 token
     * @param parameters
     * @param clientDetails
     * @return
     * @throws BusinessException
     */
    private JWTAccessToken authorizationCodeTokenGranter(Map<String, String> parameters, ClientDetails clientDetails) throws BusinessException {
        String authorizationCode = parameters.get("code");
        String redirectUri = parameters.get(OAuth2Utils.REDIRECT_URI);

        if (!StringUtils.hasText(authorizationCode)) {
            throw new BusinessException("authorization_code 不能为空。");
        }

        OAuth2Authentication storedAuth = authorizationCodeServices.consumeAuthorizationCode(authorizationCode);
        if (storedAuth == null) {
            throw new BusinessException("非法的 authorization_code：" + authorizationCode);
        }

        OAuth2Request pendingOAuth2Request = storedAuth.getStoredRequest();
        String redirectUriApprovalParameter = pendingOAuth2Request.getRequestParameters().get(
                OAuth2Utils.REDIRECT_URI);

        if (!pendingOAuth2Request.getRedirectUri().equals(redirectUri)) {
            if ((redirectUri != null || redirectUriApprovalParameter != null)) {
                throw new BusinessException("Redirect URI 不匹配。");
            }
        }

        String pendingClientId = pendingOAuth2Request.getClientId();
        String clientId = parameters.get(OAuth2Utils.CLIENT_ID);
        if (clientId != null && !clientId.equals(pendingClientId)) {
            throw new BusinessException("Client ID 不匹配。");
        }

        JWTAccessToken jwtAccessToken = createAccessToken(storedAuth.getUserDetails(), clientDetails);
        return jwtAccessToken;
    }

    /**
     * 密码登录获取 token
     * @param parameters
     * @param clientDetails
     * @return
     * @throws BusinessException
     */
    private JWTAccessToken passwordTokenGranter(Map<String, String> parameters, ClientDetails clientDetails) throws BusinessException {
        if (clientDetails.getAuthorizedGrantTypes() != null
                && !clientDetails.getAuthorizedGrantTypes().contains(JWTConstant.GT_PASSWORD)) {
            throw new BusinessException("应用【" + clientDetails.getClientId() + "】不支持密码模式登录");
        }

        String username = parameters.get(JWTConstant.PARA_USERNAME);
        String password = parameters.get(JWTConstant.PARA_PASSWORD);

        if (!StringUtils.hasText(username)) {
            throw new BusinessException("username 不能为空");
        }
        if (!StringUtils.hasText(password)) {
            throw new BusinessException("password 不能为空");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null || !encoder.matches(password, userDetails.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        JWTAccessToken jwtAccessToken = createAccessToken(userDetails,clientDetails);
        return jwtAccessToken;
    }

    /**
     * 短信登录获取 token
     * @param parameters
     * @param clientDetails
     * @return
     * @throws BusinessException
     */
    private JWTAccessToken smsTokenGranter(Map<String, String> parameters, ClientDetails clientDetails) throws BusinessException {
        if (clientDetails.getAuthorizedGrantTypes() != null
                && !clientDetails.getAuthorizedGrantTypes().contains(JWTConstant.GT_SMS)) {
            throw new BusinessException("应用【" + clientDetails.getClientId() + "】不支持短信模式登录");
        }

        String mobileNumber = parameters.get(JWTConstant.PARA_MOBILE_NUMBER);
        String verifyCode = parameters.get(JWTConstant.PARA_VERIFY_CODE);
        if (!StringUtils.hasText(mobileNumber)) {
            throw new BusinessException("mobile_number 不能为空");
        }
        if (!StringUtils.hasText(verifyCode)) {
            throw new BusinessException("verify_code 不能为空");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(mobileNumber);
        if (userDetails == null) {
            throw new BusinessException("该手机号未注册");
        }

        Boolean successVerify = smsService.hasSmsVerifyCodeExpired(mobileNumber, verifyCode,
                smsConfig.getKjdldxTemplate().getTemplateCode());

        if (!successVerify) {
            throw new BusinessException("短信验证码无效，请重新登录。");
        }
        smsService.updateSmsVerifyCodeIntoExpired(mobileNumber, smsConfig.getKjdldxTemplate().getTemplateCode());

        JWTAccessToken jwtAccessToken = createAccessToken(userDetails,clientDetails);
        return jwtAccessToken;
    }

    /**
     * 刷新 token
     * @param parameters
     * @param clientDetails
     * @return
     * @throws BusinessException
     */
    private JWTAccessToken refreshTokenGranter(Map<String, String> parameters, ClientDetails clientDetails) throws BusinessException {
        String refreshToken = parameters.get(JWTConstant.GT_REFRESH_TOKEN);
        if (!StringUtils.hasText(refreshToken)) {
            throw new BusinessException("refresh_token 不能为空。");
        }

        TokenExtractResult result = TokenHelper.extract(refreshToken,clientDetailsService);

        if(result.getCode()==-1){
            throw new BusinessException("refresh_token 校验失败");
        }
        if(result.getCode()==0){
            throw new BusinessException("refresh_token 已过期");
        }
        if(result.getCode()==1){
            UserDetails userDetails = userDetailsService.loadUserByUsername(result.getUserName());
            if (userDetails == null) {
                throw new BusinessException("该用户不存在");
            }
            JWTAccessToken jwtAccessToken = createAccessToken(userDetails, clientDetails);
            return jwtAccessToken;
        }
        throw new BusinessException(StandResponse.INTERNAL_SERVER_ERROR,"系统异常");
    }

    /**
     * 创建 AccessToken
     * @param userDetails
     * @param clientDetails
     * @return
     */
    private JWTAccessToken createAccessToken(UserDetails userDetails, ClientDetails clientDetails) {
        JWTAccessToken token = new JWTAccessToken();
        int validitySeconds = getAccessTokenValiditySeconds(clientDetails);
        if (validitySeconds > 0) {
            token.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
        }

        int refreshTokenValiditySeconds = getRefreshTokenValiditySeconds(clientDetails);
        if(refreshTokenValiditySeconds >0) {
            token.setRefreshTokenExpiration(new Date(System.currentTimeMillis() + (refreshTokenValiditySeconds * 1000L)));
        }

        token.setUserId(userDetails.getUserId());
        token.setUserName(userDetails.getUsername());
        token.setClientId(clientDetails.getClientId());
        token.setJti(UUID.randomUUID().toString());

        convert(token,clientDetails.getClientSecret());

        return token;
    }

    /**
     * 构造 JWTAccessToken
     * @param jwtAccessToken
     * @param secretKey
     */
    public static void convert(JWTAccessToken jwtAccessToken, String secretKey){
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String token = JWT.create()
                .withClaim(JWTConstant.CLAIM_CLIENT_ID, jwtAccessToken.getClientId())
                .withClaim(JWTConstant.CLAIM_USER_NAME, jwtAccessToken.getUserName())
                .withClaim(JWTConstant.CLAIM_USER_ID, jwtAccessToken.getUserId())
                .withExpiresAt(jwtAccessToken.getExpiration())
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);

        jwtAccessToken.setAccessToken(token);

        if(jwtAccessToken.getNeedRefreshToken()){
            String refresh_token = JWT.create()
                    .withClaim(JWTConstant.CLAIM_CLIENT_ID, jwtAccessToken.getClientId())
                    .withClaim(JWTConstant.CLAIM_USER_NAME, jwtAccessToken.getUserName())
                    .withClaim(JWTConstant.CLAIM_USER_ID, jwtAccessToken.getUserId())
                    .withExpiresAt(jwtAccessToken.getExpiration())
                    .withJWTId(UUID.randomUUID().toString())
                    .sign(algorithm);
            jwtAccessToken.setRefreshToken(refresh_token);
        }
    }

    /**
     * 获取 AccessToken 过期时间
     * @param clientDetails
     * @return
     */
    private int getAccessTokenValiditySeconds(ClientDetails clientDetails) {
        if (clientDetails != null) {
            Integer validity = clientDetails.getAccessTokenValiditySeconds();
            if (validity != null) {
                return validity;
            }
        }
        return accessTokenValiditySeconds;
    }

    /**
     * 获取 RefreshToken 过期时间
     * @param clientDetails
     * @return
     */
    private int getRefreshTokenValiditySeconds(ClientDetails clientDetails) {
        if (clientDetails != null) {
            Integer validity = clientDetails.getRefreshTokenValiditySeconds();
            if (validity != null) {
                return validity;
            }
        }
        return refreshTokenValiditySeconds;
    }
}
