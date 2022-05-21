package com.spring.session.controller;

import com.spring.session.bean.AuthenticationRequest;
import com.spring.session.bean.UserDto;
import com.spring.session.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author security
 */
@RestController
public class LoginController {

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = "/login",produces="text/plain;charset=UTF-8")
    public String login(AuthenticationRequest authenticationRequest,HttpSession session){
        UserDto userDto = authenticationService.authentication(authenticationRequest);

        //保存session信息
        session.setAttribute(UserDto.SESSION_USER_KEY,userDto);

        return userDto.getFullname()+"登录成功";
    }

    @RequestMapping(value = "/logout",produces="text/plain;charset=UTF-8")
    public String logout(HttpSession session){
        session.invalidate();
        return "退出成功";
    }


    @RequestMapping(value = "/r/r1",produces="text/plain;charset=UTF-8")
    public String r1(HttpSession session){
        String fullname = null;
        Object object = session.getAttribute(UserDto.SESSION_USER_KEY);
        if (object == null) {
            fullname = "匿名";
        }else {
            UserDto userDto = (UserDto) object;
            fullname = userDto.getFullname();
        }
        return fullname+"访问资源";

    }

    @RequestMapping(value = "/r/r2",produces="text/plain;charset=UTF-8")
    public String r2(HttpSession session){
        String fullname = null;
        Object object = session.getAttribute(UserDto.SESSION_USER_KEY);
        if (object == null) {
            fullname = "匿名";
        }else {
            UserDto userDto = (UserDto) object;
            fullname = userDto.getFullname();
        }
        return fullname+"访问资源";

    }


}
