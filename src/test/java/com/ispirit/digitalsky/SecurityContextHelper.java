package com.ispirit.digitalsky;

import com.ispirit.digitalsky.domain.User;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.domain.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;

import static java.util.Collections.emptyList;

public class SecurityContextHelper {

    public static UserPrincipal setUserSecurityContext() {
        UserPrincipal userPrincipal = new UserPrincipal(user());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return userPrincipal;
    }

    public static UserPrincipal setAdminUserSecurityContext() {
        UserPrincipal userPrincipal = new UserPrincipal(adminUser());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return userPrincipal;
    }

    public static User user() {
        return new User(1, "name", "user@email.com", "password", emptyList());
    }

    public static User adminUser() {
        ArrayList<UserRole> roles = new ArrayList<>();
        roles.add(new UserRole(1,"ROLE_ADMIN"));
        return new User(2, "admin", "admin@email.com", "password", roles);

    }
}
