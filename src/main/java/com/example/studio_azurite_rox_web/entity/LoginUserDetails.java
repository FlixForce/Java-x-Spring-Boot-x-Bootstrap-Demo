package com.example.studio_azurite_rox_web.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class LoginUserDetails implements UserDetails {
    private final LoginUser loginUser;

    private final Collection<? extends GrantedAuthority> authorities;

    public LoginUserDetails(LoginUser loginUser) {
        this.loginUser = loginUser;
        this.authorities = loginUser.roleList()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .toList();
    }

    public LoginUser getLoginUser() {
        return loginUser;
    }

    public Integer getId() {
        return loginUser.id();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // ハッシュ化済みのパスワードを生成する
    @Override
    public String getPassword() {
        return loginUser.password();
    }

    // ログインで利用するユーザー名を返す
    @Override
    public String getUsername() {
        return loginUser.email();
    }

    // ユーザーが期限切れでなければ、trueを返す
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // ユーザーがロックされていなければ、trueを返す
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // ユーザーのパスワードが期限切れでなければ、trueを返す
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // ユーザーが有効であれば、trueを返す
    @Override
    public boolean isEnabled() {
        return true;
    }
}
