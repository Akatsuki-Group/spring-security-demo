package com.spring.security.service;


import com.spring.security.bean.Permission;

import java.util.List;

/**
 * @author security
 */
public interface PermissionService  {

    List<Permission> selectByUserId(Long userId);
}
