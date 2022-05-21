package com.luban.jwtdemo.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 匿名用户访问资源时无权限的处理
 */
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {
 
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
                       AuthenticationException authException) throws IOException, ServletException {
    response.setCharacterEncoding("utf-8");
    response.setContentType("text/javascript;charset=utf-8");
    response.getWriter().print(JSON.toJSONString("未登录，没有访问权限"));
  }
}