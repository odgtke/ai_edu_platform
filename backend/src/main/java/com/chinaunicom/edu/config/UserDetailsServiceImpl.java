package com.chinaunicom.edu.config;

import com.chinaunicom.edu.entity.User;
import com.chinaunicom.edu.service.UserService;
import com.chinaunicom.edu.config.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.lambdaQuery()
                .eq(User::getUsername, username)
                .one();

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return new UserDetailsImpl(user);
    }
}