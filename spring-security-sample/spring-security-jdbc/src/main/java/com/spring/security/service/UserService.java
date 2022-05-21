package com.spring.security.service;

import com.spring.security.bean.User;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author security
 */
public interface UserService extends UserDetailsService {

    User getByUsername(String username);
}
