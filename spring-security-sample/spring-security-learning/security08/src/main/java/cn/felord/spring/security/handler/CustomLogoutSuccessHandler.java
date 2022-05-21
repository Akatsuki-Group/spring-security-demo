package cn.felord.spring.security.handler;

import cn.felord.spring.security.entity.RestBody;
import cn.felord.spring.security.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CustomLogoutSuccessHandler
 *
 * @author Felordcn
 * @since 17:10 2019/10/23
 **/
@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();
        log.info("username: {}  is offline now", username);


        ResponseUtil.responseJsonWriter(response, RestBody.ok("退出成功"));
    }


}
