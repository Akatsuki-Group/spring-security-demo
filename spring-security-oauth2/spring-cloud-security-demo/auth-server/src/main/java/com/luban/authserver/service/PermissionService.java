package com.luban.authserver.service;

import com.luban.authserver.bean.SysPermission;

import java.util.List;

/**
 * @author Fox
 */
public interface PermissionService  {

    List<SysPermission> selectByUserId(Long userId);
}
