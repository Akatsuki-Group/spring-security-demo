package com.spring.security.config;

import com.spring.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
 * @author tian
 */
@Configuration
public class WebSecurityConfig3 extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("fox")
//                .password(passwordEncoder().encode("123456"))
//                .authorities("admin").and()
//                .withUser("fox2")
//                .password(passwordEncoder().encode("123456"))
//                .authorities("admin");
        auth.userDetailsService(userService);
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
                //认证失败之后转发的路径,必须是Post请求
                .failureForwardUrl("/toerror")
                .and().authorizeRequests()
                //设置哪些路径可以直接访问，不需要认证
                .antMatchers("/user/login", "/admin/demo", "/login.html", "/error.html").permitAll()
                .anyRequest().authenticated()  //需要认证
                .and().csrf().disable(); //关闭csrf防护
        //自定义退出登陆页面
        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.html");
        //记住我
        http.rememberMe()
                //设置持久化仓库
                .tokenRepository(persistentTokenRepository())
                //超时时间,单位s 默认两周
                .tokenValiditySeconds(3600)
                //设置自定义登录逻辑
                .userDetailsService(userService);

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Autowired
    public DataSource dataSource;

    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        //设置数据源
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("123456"));
    }
}
