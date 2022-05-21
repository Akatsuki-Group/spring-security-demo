package com.spring.security.service.impl;

import com.spring.security.bean.Permission;
import com.spring.security.bean.User;
import com.spring.security.mapper.PermissionMapper;
import com.spring.security.mapper.UserMapper;
import com.spring.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author security
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public User getByUsername(String username) {
        return userMapper.getByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("自定义登录逻辑");
        //从mysql查询用户
        User user = getByUsername(username);
        List<GrantedAuthority> authorities = new ArrayList<>();
        if(user != null){
            List<Permission> permissions = permissionMapper.selectByUserId(user.getId());
            //设置权限
            permissions.forEach(permission -> {
                if (permission!=null && !StringUtils.isEmpty(permission.getEnname())){
                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(permission.getEnname());
                    authorities.add(grantedAuthority);
                }
            });
            //authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            // 封装成UserDetails的实现类
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),user.getPassword(),authorities);
        }else {
            throw new UsernameNotFoundException("用户名不存在");
        }

    }
}
