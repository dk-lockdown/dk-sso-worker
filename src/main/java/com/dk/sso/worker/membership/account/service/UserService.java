package com.dk.sso.worker.membership.account.service;

import com.dk.foundation.common.SnowflakeIdGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.dk.foundation.engine.baseentity.PageResult;
import com.dk.foundation.engine.exception.BusinessException;
import com.dk.sso.worker.membership.account.entity.User;
import com.dk.sso.worker.membership.account.mapper.UserMapper;
import com.dk.sso.worker.membership.application.entity.Application;
import com.dk.sso.worker.membership.application.mapper.ApplicationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liuxiaoming
 */
@Service
public class UserService {
    private PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Resource
    UserMapper userMapper;

    @Resource
    private ApplicationMapper applicationMapper;

    /**
     * 注册用户
     *
     * @param user
     */
    public void registerUser(User user, String appId, String appSecret) throws BusinessException {
        //检查基础信息
        Application application = applicationMapper.selectApplicationByAppId(appId);
        if (application == null) {
            throw new BusinessException("应用不存在：" + application.getAppName());
        }
        User oldUser = userMapper.selectUserByUserName(user.getUserName());
        if (oldUser != null) {
            throw new BusinessException("用户名已经存在：" + oldUser.getUserName());
        }
        if (!encoder.matches(appSecret, application.getAppSecretEncrypted())) {
            throw new BusinessException("应用密码不匹配：" + application.getAppName());
        }
        user.setUserId(SnowflakeIdGenerator.getInstance().nextId());
        user.setPassword(encoder.encode(user.getPassword()));
        user.setPasswordFormat(1);
        user.setAppId(application.getAppId());
        userMapper.insertUser(user);
    }

    public User selectUserByUserId(Long userId) {
        return userMapper.selectUserByUserId(userId);
    }

    public User selectUserByUserName(String userName) {
        return userMapper.selectUserByUserName(userName);
    }

    public PageResult<User> selectUsersByPager(String userName, Integer pageIndex, Integer pageSize) {
        PageHelper.startPage(pageIndex, pageSize);
        List<User> users = userMapper.selectUsersByPager(userName);
        PageInfo<User> pageInfo = new PageInfo<>(users);
        PageResult<User> result = new PageResult<>(users, pageInfo.getTotal(), pageIndex, pageSize);
        return result;
    }

    /**
     * 修改密码
     *
     * @param userName
     * @param oldPassword
     * @param newPassword
     */
    public void updatePassword(String userName, String oldPassword, String newPassword) throws BusinessException {
        //检查基础信息
        User oldUser = userMapper.selectUserByUserName(userName);
        if (oldUser == null) {
            throw new BusinessException("用户名不存在: " + userName);
        }
        if (!encoder.matches(oldPassword, oldUser.getPassword())) {
            throw new BusinessException("原密码不正确");
        }
        userMapper.updateUserPassword(oldUser.getUserId(), encoder.encode(newPassword));
    }

    /**
     * 重置密码，此接口不能直接暴露给前台
     *
     * @param userName
     * @param newPassword
     */
    public void updatePassword(String userName, String newPassword) throws BusinessException {
        //检查基础信息
        User oldUser = userMapper.selectUserByUserName(userName);
        if (oldUser == null) {
            throw new BusinessException("用户名不存在: " + userName);
        }
        userMapper.updateUserPassword(oldUser.getUserId(), encoder.encode(newPassword));
    }

    public void bindMobilePhone(String userName, String mobileNumber) {
        userMapper.bindMobilePhone(userName, mobileNumber);
    }


    public void updateAvatar(String userName, String nickname) {
        userMapper.updateAvatar(userName, nickname);
    }

    public void updateNickName(String userName, String nickname) {
        userMapper.bindMobilePhone(userName, nickname);
    }

    public void disableUser(String userName) {
        userMapper.disableUser(userName);
    }

    public void enableUser(String userName) {
        userMapper.enableUser(userName);
    }

    public void updateIsApproved(String userName, Boolean isApproved) {
        userMapper.updateIsApproved(userName, isApproved);
    }

}