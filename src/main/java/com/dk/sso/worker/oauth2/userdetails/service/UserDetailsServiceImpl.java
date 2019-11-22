package com.dk.sso.worker.oauth2.userdetails.service;

import com.alibaba.druid.util.StringUtils;
import com.dk.foundation.engine.exception.BusinessException;
import com.dk.sso.worker.membership.account.entity.Role;
import com.dk.sso.worker.membership.account.entity.User;
import com.dk.sso.worker.membership.account.service.RoleService;
import com.dk.sso.worker.membership.account.service.UserService;
import com.dk.sso.worker.oauth2.userdetails.entity.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by duguk on 2018/3/13.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserService userService;

    @Resource
    private RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String s) throws BusinessException {
        if (StringUtils.isEmpty(s)) {
            throw new BusinessException("用户名不能为空");
        }
        User user = userService.selectUserByUserName(s);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return new UserDetails(user);
    }
}
