package com.spring.security.service.impl;


import com.spring.security.bean.Permission;
import com.spring.security.mapper.PermissionMapper;
import com.spring.security.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author security
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;
    @Override
    public List<Permission> selectByUserId(Long userId) {

        return permissionMapper.selectByUserId(userId);
    }
}
