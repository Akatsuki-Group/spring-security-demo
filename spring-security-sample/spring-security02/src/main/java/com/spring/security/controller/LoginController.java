package com.spring.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Fox
 */
@Controller
public class LoginController {

    @RequestMapping("/showLogin")
    public String showLogin() {
        return "login";
    }

    @RequestMapping("/main")
    public String main() {
        return "redirect:/main.html";
    }

    @RequestMapping("/toerror")
    public String error() {
        return "redirect:/error.html";
    }

    @RequestMapping("/demo")
    public String demo(){
        return "demo";
    }


}
