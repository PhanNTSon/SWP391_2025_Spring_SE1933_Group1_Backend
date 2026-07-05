package com.se1933g01.steamclonebackend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.se1933g01.steamclonebackend.entity.user.CustomUserDetail;
import com.se1933g01.steamclonebackend.entity.user.User;

/**
 * @author Phan NT Son
 * 
 *         Class for UserDetails of Security Starter, use for Security Context
 * 
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        try {
            User user = userService.getUser(username);
            return new CustomUserDetail(user);
        } catch (RuntimeException e) {
            throw new UsernameNotFoundException("User not found", e);
        }
    }

}
