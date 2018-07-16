package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    User findUserById(long id);

    User find(long id);

    void generateResetPasswordLink(String email);

    void resetPassword(String token, String newPassword);

    User loadByEmail(String email);

    User createNew(User user);
}
