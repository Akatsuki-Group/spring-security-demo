package com.example.security.config;

import com.example.security.filter.VerifyCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        String password = passwordEncoder().encode("123456");
        auth
                // 使用基于内存的 InMemoryUserDetailsManager
                .inMemoryAuthentication()
                //使用 PasswordEncoder 密码编码器
                //.passwordEncoder(passwordEncoder())
                // 配置用户
                .withUser("security").password(password).roles("admin")
                // 配置其他用户
                .and()
                .withUser("security2").password(password).roles("user");

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //return NoOpPasswordEncoder.getInstance();
        return new BCryptPasswordEncoder();
    }

    @Autowired
    VerifyCodeFilter verifyCodeFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(verifyCodeFilter, UsernamePasswordAuthenticationFilter.class);
        http.formLogin();
        http.authorizeRequests()
                .antMatchers("/login.html").permitAll()
                .and()
                .csrf().disable();
    }
}