package com.luban.authserver.service;

import com.luban.authserver.bean.SysUser;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author Fox
 */
public interface UserService extends UserDetailsService {

    SysUser getByUsername(String username);
}
