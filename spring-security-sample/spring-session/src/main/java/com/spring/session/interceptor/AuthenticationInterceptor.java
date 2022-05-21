package com.spring.session.interceptor;

import com.spring.session.bean.UserDto;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author security
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException, ServletException {
        //获取session信息
        Object obj = request.getSession().getAttribute(UserDto.SESSION_USER_KEY);
        if(obj == null){
            writeContext(response,"请登录");
        }
        UserDto userDto = (UserDto) obj;
        // 权限的判断
        String uri = request.getRequestURI();
        if(userDto.getAuthorities().contains("p1")&&uri.contains("/r1")){
            return true;
        }
        writeContext(response,"权限不足，拒绝访问");
        return false;
    }

    private void writeContext(HttpServletResponse response,String msg) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(msg);
        writer.flush();
        writer.close();
    }
}
