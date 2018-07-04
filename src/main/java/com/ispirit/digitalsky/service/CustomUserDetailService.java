package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.User;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailService implements UserDetailsService {


    private UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.loadByEmail(email);
        return new UserPrincipal(user.getEmail(), user.getPassword());
    }
}
