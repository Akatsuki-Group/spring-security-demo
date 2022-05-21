package com.spring.session.service;

import com.spring.session.bean.AuthenticationRequest;
import com.spring.session.bean.UserDto;
import com.spring.session.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author security
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService{
    @Autowired
    private UserMapper userMapper;
    @Override
    public UserDto authentication(AuthenticationRequest authenticationRequest) {
        if(authenticationRequest == null
                ||StringUtils.isEmpty(authenticationRequest.getUsername())
                ||StringUtils.isEmpty(authenticationRequest.getPassword())){
            throw new RuntimeException("账号或密码为空");
        }
        UserDto userDto =
                userMapper.getUserByUsername(authenticationRequest.getUsername());
        if (userDto ==null){
            throw new RuntimeException("账号不存在");
        }
        if(!authenticationRequest.getPassword().equals(userDto.getPassword())){
            throw new RuntimeException("账号或密码错误");
        }
        // 模拟用户权限
        Set<String> sets = new HashSet<>();
        sets.add("p1");
        userDto.setAuthorities(sets);
        return userDto;
    }
}
