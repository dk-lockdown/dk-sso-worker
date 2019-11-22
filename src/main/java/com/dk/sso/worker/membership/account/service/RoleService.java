package com.dk.sso.worker.membership.account.service;

import com.dk.sso.worker.membership.account.entity.Role;
import com.dk.sso.worker.membership.account.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RoleService {
    @Resource
    private RoleMapper roleMapper;

    public List<Role> getRolesByUserId(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }
}
