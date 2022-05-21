package com.spring.security.controller;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin")
//@PermitAll
public class AdminController {

    //@DenyAll
    //@PermitAll
    //@Secured("ROLE_admin1")
    //@RolesAllowed({"user"})
    @PreAuthorize("permitAll()")
    @GetMapping("/demo")
    public String demo() {
        return "spring security demo";
    }

    @PreAuthorize("#id<4")
    @GetMapping("/index")
    public String index(int id) {
        return "spring security demo";
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

    // 在方法find()调用完成后进行权限检查，如果返回值的id是偶数则表示校验通过，否则表示校验失败，将抛出AccessDeniedException
    @PostAuthorize("returnObject.id%2==0")
    public User find(int id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    @PostFilter("filterObject.id%2==0")
    public List<User> findAll() {
        List<User> userList = new ArrayList<User>();
        User user;
        for (int i = 0; i < 10; i++) {
            user = new User();
            user.setId(i);
            userList.add(user);
        }
        return userList;
    }

    @PreFilter(filterTarget = "ids", value = "filterObject%2==0")
    public void delete(List<Integer> ids, List<String> usernames) {

    }

    public class User {
        private int id;
        private String username;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}