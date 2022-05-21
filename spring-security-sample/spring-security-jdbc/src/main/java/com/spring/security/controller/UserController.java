package com.spring.security.controller;

import com.spring.security.bean.User;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Fox
 */
@RestController
@RequestMapping("/user")
public class UserController {


    //@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    //限制只能查询Id小于10的用户
    @PreAuthorize("#id<10")
    @PostAuthorize("returnObject.id%2==0")
    @RequestMapping("/findById")
    public User findById(long id) {
        User user = new User();
        user.setId(id);
        return user;
    }


    // 限制只能查询自己的信息
    @PreAuthorize("principal.username.equals(#username)")
    @RequestMapping("/findByName")
    public User findByName(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }

    //限制只能新增用户名称为abc的用户
    @PreAuthorize("#user.username.equals('abc')")
    @RequestMapping("/add")
    public User add(User user) {
        return user;
    }


    @PostFilter("filterObject.id%2==0")
    @RequestMapping("/findAll")
    public List<User> findAll() {
        List<User> userList = new ArrayList<User>();
        User user;
        for (long i=0; i<10; i++) {
            user = new User();
            user.setId(i);
            userList.add(user);
        }
        return userList;
    }

}
