package com.luban.authserver.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Fox
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private TokenStore tokenService;

//    @RequestMapping("/getCurrentUser")
//    public Object getCurrentUser(Authentication authentication) {
//        return authentication.getPrincipal();
//    }

    @RequestMapping("/getCurrentUser")
    public Object getCurrentUser(@RequestParam("access_token") String token) {
        OAuth2Authentication oAuth2Authentication =  tokenService.readAuthentication(token);

        return oAuth2Authentication.getUserAuthentication().getPrincipal();
    }
}
