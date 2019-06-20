package com.ispirit.digitalsky.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class UserPrincipal implements UserDetails {
    private String name;
    private long id;
    private String email;
    private String password;
    private List<GrantedAuthority> authorityList;
    private boolean accountVerified = false;
    private String region;

    public static UserPrincipal securityContext() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.name = user.getFullName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.accountVerified = user.isAccountVerified();
        this.authorityList = unmodifiableList(toAuthorityGrantList(user.getRoleNames()));
        this.region = user.getFir();

    }

    private List<GrantedAuthority> toAuthorityGrantList(List<String> roles) {
        List<GrantedAuthority> authorityGrantList = new ArrayList<>();
        if (roles == null) return authorityGrantList;
        roles.forEach(role -> authorityGrantList.add(new SimpleGrantedAuthority(role)));
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
        return name;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
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

    public boolean isAccountVerified() {
        return accountVerified;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isAdmin() {
        for (GrantedAuthority grantedAuthority : authorityList) {
            if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) return true;
        }
        return false;
    }

    public boolean isAtcAdmin() {
        for (GrantedAuthority grantedAuthority : authorityList) {
            if (grantedAuthority.getAuthority().equals("ROLE_ATC_ADMIN")) return true;
        }
        return false;
    }

    public boolean isAfmluAdmin() {
        for (GrantedAuthority grantedAuthority : authorityList) {
            if (grantedAuthority.getAuthority().equals("ROLE_AFMLU_ADMIN")) return true;
        }
        return false;
    }

    public boolean isViewerAdmin(){
        for (GrantedAuthority grantedAuthority : authorityList) {
            if (grantedAuthority.getAuthority().equals("ROLE_VIEWER_ADMIN")) return true;
        }
        return false;
    }
    public boolean isATCViewerAdmin(){
        for (GrantedAuthority grantedAuthority : authorityList) {
            if (grantedAuthority.getAuthority().equals("ROLE_ATC_VIEWER_ADMIN")) return true;
        }
        return false;
    }
    public boolean isAFMLUViewerAdmin(){
        for (GrantedAuthority grantedAuthority : authorityList) {
            if (grantedAuthority.getAuthority().equals("ROLE_AFMLU_VIEWER_ADMIN")) return true;
        }
        return false;
    }

    public String getRegion() {
        return region;
    }
}
