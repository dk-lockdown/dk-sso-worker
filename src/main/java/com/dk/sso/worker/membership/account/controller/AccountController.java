package com.dk.sso.worker.membership.account.controller;

import com.dk.foundation.engine.baseentity.PageResult;
import com.dk.foundation.engine.baseentity.StandResponse;
import com.dk.foundation.engine.exception.BusinessException;
import com.dk.sso.worker.membership.account.controller.vo.*;
import com.dk.sso.worker.membership.account.entity.User;
import com.dk.sso.worker.membership.account.service.UserService;
import com.dk.sso.worker.membership.interceptor.service.AccountSvcImpl;
import com.dk.startup.worker.interceptor.annotation.Login;
import com.dk.startup.worker.interceptor.annotation.Permission;
import com.dk.startup.worker.interceptor.entity.TokenExtractResult;
import com.dk.startup.worker.util.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Controller
@RequestMapping("/v1/account")
@Api(value = "用户", produces = "application/json;charset=UTF-8")
@Validated
public class AccountController extends BaseController {
    final static Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Resource
    private UserService userService;

    @Resource
    private AccountSvcImpl accountSvc;

    @ApiOperation(value = "用户注册", notes = "")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public @ResponseBody
    StandResponse registerUser(@RequestBody @Valid UserRegisterVO registerUserVO) throws BusinessException {
        User user = new User();
        user.setUserName(registerUserVO.getUserName());
        user.setPassword(registerUserVO.getPassWord());
        userService.registerUser(user, registerUserVO.getAppId(), registerUserVO.getAppSecret());
        return success();
    }

    @ApiOperation(value = "用户查询", notes = "")
    @ApiImplicitParam(name = "Authorization", value = "获取accessToken后，传入Bearer +token访问", paramType = "header")
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    @Login
    public @ResponseBody
    StandResponse<User> getUserInfo() {
        return success(userService.selectUserByUserName(getUserName()));
    }

    @ApiOperation(value = "通过用户名获取用户信息", notes = "具有SSO角色的管理员才能通过此接口获取用户信息")
    @ApiImplicitParam(name = "Authorization", value = "获取accessToken后，传入Bearer +token访问", paramType = "header")
    @RequestMapping(value = "/getUserInfoByUserName", method = RequestMethod.GET)
    @Permission(permissionKey = "sso-tgyhmhqyhxx")
    public @ResponseBody
    StandResponse<User> getUserInfoByUserName(@RequestParam("username") String username) {
        return success(userService.selectUserByUserName(username));
    }

    @ApiOperation(value = "分页查询用户信息", notes = "具有SSO角色的管理员才能通过此接口获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "获取accessToken后，传入Bearer +token访问", paramType = "header"),
    })
    @RequestMapping(value = "/selectUsersByPager", method = RequestMethod.POST)
    @Permission(permissionKey = "sso-cxyhxx")
    public @ResponseBody
    StandResponse<PageResult<User>> selectUsersByPager(@RequestBody UserQueryRequestVO requestVO) {
        return success(userService.selectUsersByPager(requestVO.getUserName(), requestVO.getPageIndex(),
                requestVO.getPageSize()));
    }


    @ApiOperation(value = "用户修改密码", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "获取accessToken后，传入Bearer +token访问", paramType = "header"),
    })
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @Login
    public @ResponseBody
    StandResponse updatePwd(@RequestBody @Valid UpdatePasswordRequestVO requestVO) throws BusinessException {
        userService.updatePassword(getUserName(), requestVO.getPassWord(), requestVO.getNewPassWord());
        return success();
    }

    @ApiOperation(value = "通过用户名获取用户信息是否存在", notes = "通过用户名获取用户信息是否存在")
    @RequestMapping(value = "/getUserIsExist", method = RequestMethod.GET)
    public @ResponseBody
    StandResponse<Boolean> getUserIsExistByUserName(@RequestParam("username") String username) {
        User user = userService.selectUserByUserName(username);
        return success(user != null);
    }

    @ApiOperation(value = "更新头像", notes = "")
    @ApiImplicitParam(name = "Authorization", value = "获取accessToken后，传入Bearer +token访问", paramType = "header")
    @RequestMapping(value = "/updateAvatar", method = RequestMethod.POST)
    @Login
    public @ResponseBody
    StandResponse updateAvatar(@RequestBody @Valid UpdateAvatarRequestVO request) {
        userService.updateAvatar(getUserName(), request.getAvatar());
        return success();
    }

    @ApiOperation(value = "用户查询", notes = "")
    @ApiImplicitParam(name = "Authorization", value = "获取accessToken后，传入Bearer +token访问", paramType = "header")
    @RequestMapping(value = "/updateNickName", method = RequestMethod.POST)
    @Login
    public @ResponseBody
    StandResponse updateNickName(@RequestBody @Valid UpdateNickNameRequestVO request) {
        userService.updateNickName(getUserName(), request.getNickname());
        return success();
    }

    /**
     * @warn 不能加@Login @HasRole @Permission标签，否则死循环
     * @return
     */
    @ApiOperation(value = "验证 Token 并提取用户信息", notes = "")
    @RequestMapping(value = "/tokenExtract", method = RequestMethod.GET)
    public @ResponseBody StandResponse<TokenExtractResult> tokenExtract(@RequestHeader("Authorization") String token) {
        TokenExtractResult result = accountSvc.tokenExtract(token);
        return result.getCode()==-1? fail(StandResponse.INTERNAL_SERVER_ERROR,"校验用户token失败") : success(result);
    }
}