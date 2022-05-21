package com.spring.security.config;

import com.spring.security.handle.MyAuthenticationFailureHandler;
import com.spring.security.handle.MyAuthenticationSuccessHandler;
import com.spring.security.handle.MyLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author tian
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService myUserDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("fox")
//                .password(passwordEncoder().encode("123456"))
//                .authorities("admin").and()
//                .withUser("fox2")
//                .password(passwordEncoder().encode("123456"))
//                .authorities("admin");
        auth.userDetailsService(myUserDetailsService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**","/images/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()  //表达登录
                //指定登录路径
                .loginPage("/login.html")
                // 对应 action="/user/login" 登录访问路径，必须和表单提交接口一样
                .loginProcessingUrl("/user/login")
                //认证成功之后转发的路径,必须是Post请求
                .successForwardUrl("/main")
                .successHandler(new MyAuthenticationSuccessHandler("/admin/index"))
                .failureHandler(new MyAuthenticationFailureHandler("/toerror"))
                //认证失败之后转发的路径,必须是Post请求
                .failureForwardUrl("/toerror")
                .and().authorizeRequests()
                //设置哪些路径可以直接访问，不需要认证
                .antMatchers("/user/login", "/admin/demo", "/login.html", "/error.html").permitAll()
                .antMatchers("/**/*.js").permitAll()
                .antMatchers("/js/**","/css/**").permitAll()
                //需要认证
                .anyRequest().authenticated()
                //关闭csrf防护
                .and().csrf().disable();
        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.html")
                .logoutSuccessHandler(new MyLogoutSuccessHandler());
        //自定义退出登陆页面
        http.formLogin() //表单提交
                .usernameParameter("username1212")
                .passwordParameter("password1212");

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    @Override
//    public UserDetailsService userDetailsServiceBean() throws Exception {
//        return new MyUserDetailsService();
//    }
}
