package com.ispirit.digitalsky.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class UserPrincipal implements UserDetails {
    private String userName;
    private String password;
    private List<GrantedAuthority> authorityList;

    public UserPrincipal(String email, String password, String... roles) {

        this.userName = email;
        this.password = password;
        this.authorityList = unmodifiableList(toAuthorityGrantList(roles));


    }

    private List toAuthorityGrantList(String[] roles) {
        List authorityGrantList = new ArrayList<>();
        if (roles != null && roles.length > 1) {
            for (String role : roles) {
                authorityList.add(new SimpleGrantedAuthority(role));
            }
        }
        return authorityGrantList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorityList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
