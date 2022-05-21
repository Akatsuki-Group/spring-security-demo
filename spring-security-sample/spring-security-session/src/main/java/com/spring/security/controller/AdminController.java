package com.spring.security.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/demo")
    public String demo() {
        return "spring security demo";
    }

    @GetMapping("/index")
    public String index(HttpSession session) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return session.getId()+","+auth.getName() + "登录成功";
    }
}