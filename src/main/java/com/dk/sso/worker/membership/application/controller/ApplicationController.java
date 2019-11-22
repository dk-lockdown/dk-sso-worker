package com.dk.sso.worker.membership.application.controller;

import com.dk.foundation.engine.baseentity.PageResult;
import com.dk.foundation.engine.baseentity.StandResponse;
import com.dk.foundation.engine.exception.BusinessException;
import com.dk.sso.worker.membership.account.entity.User;
import com.dk.sso.worker.membership.account.service.UserService;
import com.dk.sso.worker.membership.application.controller.vo.ApplicationCreateVO;
import com.dk.sso.worker.membership.application.controller.vo.ApplicationQueryRequestVO;
import com.dk.sso.worker.membership.application.entity.Application;
import com.dk.sso.worker.membership.application.service.ApplicationService;
import com.dk.startup.worker.interceptor.annotation.Login;
import com.dk.startup.worker.interceptor.annotation.Permission;
import com.dk.startup.worker.util.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

/**
 * @author zhanghonghao
 * Created on 2018/11/20 上午11:22
 */
@Controller
@RequestMapping("/v1/application")
@Api(value = "应用",  produces = "application/json;charset=UTF-8")
@Validated
public class ApplicationController extends BaseController {
    @Autowired
    UserService userService;

    @Autowired
    ApplicationService applicationService;

    @ApiOperation(value = "添加应用", notes = "具有SSO角色的管理员才能添加应用。")
    @ApiImplicitParam(name = "Authorization", value = "获取accessToken后，传入Bearer +token访问", paramType = "header")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody
    StandResponse<Void> addApplication(@RequestBody @Valid ApplicationCreateVO applicationVO) throws BusinessException {
        Application application = new Application();
        application.setAppId(applicationVO.getAppId());
        application.setAppSecret(UUID.randomUUID().toString().replaceAll("\\-", ""));
        application.setAppName(applicationVO.getAppName());
        application.setGrantTypes(applicationVO.getGrantType());
        if(applicationVO.getExpireIn()==null||applicationVO.getExpireIn()<0) {
            applicationVO.setExpireIn(64000);
        }
        application.setExpireIn(applicationVO.getExpireIn());
        applicationService.insertApplication(application);
        return success();
    }

    @ApiOperation(value = "通过AppId获取应用信息", notes = "具有SSO角色的管理员才能通过此接口获取应用信息。")
    @ApiImplicitParam(name = "Authorization", value = "获取accessToken后，传入Bearer +token访问", paramType = "header")
    @RequestMapping(value = "/getApplicationByAppId", method = RequestMethod.GET)
    @Permission(permissionKey = "sso-tgidhqyy")
    public @ResponseBody
    StandResponse<Application> getApplicationInfoByAppId(@RequestParam("appId") String appId) {
        return success(applicationService.selectApplicationByAppId(appId));
    }

    @ApiOperation(value = "通过AppId获取应用信息", notes = "具有SSO角色的管理员才能通过此接口获取应用信息。")
    @ApiImplicitParam(name = "Authorization", value = "获取accessToken后，传入Bearer +token访问", paramType = "header")
    @RequestMapping(value = "/selectApplicationsByPager", method = RequestMethod.POST)
    @Permission(permissionKey = "sso-cxyy")
    public @ResponseBody
    StandResponse<PageResult<Application>> selectApplicationsByPager(@RequestBody ApplicationQueryRequestVO requestVO) {
        return success(applicationService.selectApplicationsByPager(requestVO.getAppId(),requestVO.getPageIndex(),requestVO.getPageSize()));
    }

    @ApiOperation(value = "应用查询", notes = "")
    @ApiImplicitParam(name = "Authorization", value = "获取accessToken后，传入Bearer +token访问", paramType = "header")
    @RequestMapping(value = "/getApplication", method = RequestMethod.GET)
    @Login
    public @ResponseBody
    StandResponse<Application> getApplicationInfo() {
        User user = userService.selectUserByUserName(getUserName());
        return success(applicationService.selectApplicationByAppId(user.getAppId()));
    }

}
