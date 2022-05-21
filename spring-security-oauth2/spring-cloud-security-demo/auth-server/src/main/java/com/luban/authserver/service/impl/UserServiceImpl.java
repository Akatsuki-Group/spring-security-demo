package com.luban.authserver.service.impl;


import com.luban.authserver.bean.SysPermission;
import com.luban.authserver.bean.SysUser;
import com.luban.authserver.mapper.PermissionMapper;
import com.luban.authserver.mapper.UserMapper;
import com.luban.authserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Fox
 */
@Service("userDetailsService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public SysUser getByUsername(String username) {
        return userMapper.getByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = getByUsername(username);
        List<GrantedAuthority> authorities = new ArrayList<>();
        if(user!=null){
            List<SysPermission> permissions = permissionMapper.selectByUserId(user.getId());

            permissions.forEach(permission -> {
                if (permission!=null && !StringUtils.isEmpty(permission.getEnname())){
                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(permission.getUrl());
                    authorities.add(grantedAuthority);
                }
            });
            return new User(
                    user.getUsername(),user.getPassword(),authorities);
        }else {
            throw new UsernameNotFoundException("用户名不存在");
        }

    }
}
