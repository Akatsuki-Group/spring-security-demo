package com.spring.security.config;

import com.spring.security.exception.SimpleAccessDeniedHandler;
import com.spring.security.exception.SimpleAuthenticationEntryPoint;
import com.spring.security.hander.MyAccessDeniedHandler;
import com.spring.security.service.UserDetailsRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * @author Fox
 */
@Configuration
@EnableGlobalMethodSecurity(jsr250Enabled = true, securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * 权限继承  A>B>C>D
     * @return
     */
    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_A > ROLE_B > ROLE_C > ROLE_D");
        return hierarchy;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //return NoOpPasswordEncoder.getInstance();
        return new BCryptPasswordEncoder();
    }

    /**
     * 自定义实现
     *
     * @return
     */
//    @Bean
    public UserDetailsRepository userDetailsRepository() {
        UserDetailsRepository userDetailsRepository = new UserDetailsRepository();

        // 为了让我们的登录能够运行 这里我们初始化一个用户Felordcn 密码采用明文 当你在密码12345上使用了前缀{noop} 意味着你的密码不使用加密，authorities 一定不能为空 这代表用户的角色权限集合
        UserDetails felordcn = User.withUsername("Felordcn").password("{noop}12345").authorities(AuthorityUtils.NO_AUTHORITIES).build();
        userDetailsRepository.createUser(felordcn);
        return userDetailsRepository;
    }

    //    @Bean
    public UserDetailsManager userDetailsManager(UserDetailsRepository userDetailsRepository) {
        return new UserDetailsManager() {
            @Override
            public void createUser(UserDetails user) {
                userDetailsRepository.createUser(user);
            }

            @Override
            public void updateUser(UserDetails user) {
                userDetailsRepository.updateUser(user);
            }

            @Override
            public void deleteUser(String username) {
                userDetailsRepository.deleteUser(username);
            }

            @Override
            public void changePassword(String oldPassword, String newPassword) {
                userDetailsRepository.changePassword(oldPassword, newPassword);
            }

            @Override
            public boolean userExists(String username) {
                return userDetailsRepository.userExists(username);
            }

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userDetailsRepository.loadUserByUsername(username);
            }
        };
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.formLogin();
//        http.authorizeRequests()
//                //明确指定路径/资源  设置哪些路径可以直接访问，不需要认证
//                .antMatchers("/user/login", "/login.html").permitAll()
//                //ANT风格 放行 js和css 目录下所有的文件
//                .antMatchers("/js/**", "/css/**").permitAll()
//                //ANT风格 只要是.js 文件都放行
//                .antMatchers("/**/*.js").permitAll()
//                //正则表达式匹配 所有以.js 结尾的文件都被放行
//                .regexMatchers(".+[.]js").permitAll()
//                //指定指定方法的uri
//                .antMatchers(HttpMethod.POST, "/admin/demo").permitAll()
//                //正则匹配 指定指定方法的uri
//                .regexMatchers(HttpMethod.GET, ".+[.]jpg").permitAll()
//                //配置servletPath
//                .mvcMatchers("/admin/demo").servletPath("/web").permitAll()
//                //等价于
//                .antMatchers("/web/admin/demo").permitAll()
//                .antMatchers("/admin/demo").hasRole("admin")
//                .antMatchers("/admin/demo").hasAuthority("admin")
//                .antMatchers("/admin/demo").access("hasAuthority('admin')")
//                .antMatchers("/user/login", "/login.html").access("permitAll")
//                //  localhost --> getRemoteAddr:  0:0:0:0:0:0:0:1
//                .antMatchers("/admin/demo").hasIpAddress("127.0.0.1")
//                //自定义权限认证逻辑
//                .antMatchers("/admin/demo").access("@mySecurityExpression.hasPermission(request,authentication)")
//                .anyRequest().access("@mySecurityExpression.hasPermission(request,authentication)")
//                //需要认证才能访问
//                .anyRequest().authenticated();
//
//        //自定义认证失败逻辑
//        http.exceptionHandling().accessDeniedHandler(new MyAccessDeniedHandler());
////                .regexMatchers(".+[.]jpg").permitAll()
//        //.mvcMatchers("/admin/demo").servletPath("/web").permitAll()
//        //        .anyRequest().authenticated(); //所有请求都要认证
//        http.csrf().disable();
//        http.addFilterBefore(preLoginFilter, UsernamePasswordAuthenticationFilter.class);
//
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //ProviderManager providerManager = new ProviderManager(Collections.singletonList(captchaAuthenticationProvider));
//        http.authenticationProvider(captchaAuthenticationProvider);

        http.formLogin();
        http.authorizeRequests()
                //明确指定路径/资源  设置哪些路径可以直接访问，不需要认证
                .antMatchers("/user/login", "/login.html").permitAll()
                //需要认证才能访问
                .anyRequest().authenticated();

        //自定义认证失败逻辑
        http.exceptionHandling().accessDeniedHandler(new MyAccessDeniedHandler());
        http.exceptionHandling()
                .accessDeniedHandler(new SimpleAccessDeniedHandler())
                .authenticationEntryPoint(new SimpleAuthenticationEntryPoint());
//                .regexMatchers(".+[.]jpg").permitAll()
        //.mvcMatchers("/admin/demo").servletPath("/web").permitAll()
        //        .anyRequest().authenticated(); //所有请求都要认证
        http.csrf().disable();
        //http.addFilterBefore(preLoginFilter, UsernamePasswordAuthenticationFilter.class);

    }


}
