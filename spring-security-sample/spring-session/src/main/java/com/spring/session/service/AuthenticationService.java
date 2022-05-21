package com.spring.session.service;

import com.spring.session.bean.AuthenticationRequest;
import com.spring.session.bean.UserDto;

/**
 * @author security
 */
public interface AuthenticationService {
    /**
     * 用户认证
     * @param authenticationRequest
     * @return
     */
    UserDto authentication(AuthenticationRequest authenticationRequest);
}
