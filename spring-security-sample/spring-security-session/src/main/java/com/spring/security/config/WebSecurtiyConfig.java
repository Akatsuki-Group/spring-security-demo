package com.spring.security.config;

import com.spring.security.strategy.MyExpiredSessionStrategy;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * @author Fox
 */
@Configuration
public class WebSecurtiyConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("fox")
                .password("{noop}123456").authorities("admin,user");
    }

//    @Bean
//    PasswordEncoder passwordEncoder(){
//        return NoOpPasswordEncoder.getInstance();
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin();

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .invalidSessionUrl("/session/invalid")
                // 最大会话数量
                .maximumSessions(1)
                // 配置会话过期策略
                .expiredSessionStrategy(new MyExpiredSessionStrategy())
                //阻止用户第二次登录
                .maxSessionsPreventsLogin(true);

        http.authorizeRequests()
                .antMatchers("/login", "/session/invalid").permitAll()
                .anyRequest().authenticated();
        http.csrf().disable();

    }
}
