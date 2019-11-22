package com.dk.sso.worker.membership.account.mapper;

import com.dk.sso.worker.membership.account.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {
    List<Role> selectRolesByUserId(Long userId);
}
