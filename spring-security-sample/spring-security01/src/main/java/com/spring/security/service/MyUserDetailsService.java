package com.spring.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author security
 */
//@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        // 登录逻辑
//        // mysql
//        //String pw = BCrypt.hashpw("123456",BCrypt.gensalt());
//        UserDetails userDetails = User.withUsername("security")
//                .password("{noop}123456").authorities("admin").build();
//                .password("{bcrypt}123456").authorities("admin").build();
//
//
//        return userDetails;
//    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //String hashpw = BCrypt.hashpw("123456", BCrypt.gensalt());
        String hashpw = passwordEncoder.encode("123456");
        UserDetails userDetails = User.withUsername("security")
                .password(hashpw).authorities("admin").build();

        return userDetails;
    }


}
