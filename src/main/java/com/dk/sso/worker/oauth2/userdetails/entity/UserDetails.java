package com.dk.sso.worker.oauth2.userdetails.entity;

import com.dk.sso.worker.membership.account.entity.User;

import java.io.Serializable;


/**
 * Created by duguk on 2018/3/9.
 */
public class UserDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String password;
    private Boolean status;

    public UserDetails(User user) {
        this.userId = user.getUserId();
        this.username = user.getUserName();
        this.password = user.getPassword();
        this.status = user.getIsApproved();
    }

    public Long getUserId() {
        return userId;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return status;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
