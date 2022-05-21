package com.spring.security.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {


    //@RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    //@PermitAll
    //@Secured("ROLE_ADMIN")
    @GetMapping("/demo")
    public String demo() {
        return "spring security demo";
    }


    @GetMapping("/index")
    public String index() {
        String username = getUsername();
        return username + "登录成功";
    }



    private String getUsername(){
        // 获取当前登录的用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!authentication.isAuthenticated()){
            return null;
        }
        Object principal = authentication.getPrincipal();
        if(principal == null){
            return "匿名";
        }
        String username = null;
        if (principal instanceof UserDetails) {
            username =((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        return username;
    }

}