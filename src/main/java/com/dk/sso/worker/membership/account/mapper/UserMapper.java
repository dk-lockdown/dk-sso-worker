package com.dk.sso.worker.membership.account.mapper;

import com.dk.sso.worker.membership.account.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by duguk on 2018/3/6.
 */
@Mapper
public interface UserMapper {
    void insertUser(User user);

    User selectUserByUserId(Long userId);

    User selectUserByUserName(String userName);

    List<User> selectUsersByPager(@Param("userName") String userName);

    void updateUserPassword(@Param("userId") Long userId, @Param("passWord") String passWord);

    void bindMobilePhone(@Param("userName") String userName, @Param("mobileNumber") String mobileNumber);

    void updateAvatar(@Param("userName") String userName, @Param("avatar") String avatar);

    void updateNickName(@Param("userName") String userName, @Param("nickname") String nickname);

    void updateIsApproved(@Param("userName") String userName, @Param("isApproved") Boolean isApproved);

    void disableUser(String nickname);

    void enableUser(String nickname);
}
