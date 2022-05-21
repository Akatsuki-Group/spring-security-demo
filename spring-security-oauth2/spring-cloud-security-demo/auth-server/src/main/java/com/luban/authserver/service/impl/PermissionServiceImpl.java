package com.luban.authserver.service.impl;

import com.luban.authserver.bean.SysPermission;
import com.luban.authserver.mapper.PermissionMapper;
import com.luban.authserver.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Fox
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;
    @Override
    public List<SysPermission> selectByUserId(Long userId) {

        return permissionMapper.selectByUserId(userId);
    }
}
