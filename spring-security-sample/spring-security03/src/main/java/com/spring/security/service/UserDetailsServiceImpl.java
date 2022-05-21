package com.spring.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author Fox
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //String hashpw = BCrypt.hashpw("123456", BCrypt.gensalt());
        String hashpw = passwordEncoder.encode("123456");
        UserDetails userDetails = User.withUsername("fox")
                .password(hashpw).authorities("admin", "ROLE_admin").build();
        //admin,user就是用户的权限
        UserDetails userDetails1 =  new User("fox1", hashpw,
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_admin,user"));

        return userDetails;
    }
}
