package cn.felord.spring.security.config;

import cn.felord.spring.security.captcha.CaptchaAuthenticationFilter;
import cn.felord.spring.security.captcha.CaptchaAuthenticationProvider;
import cn.felord.spring.security.captcha.CaptchaService;
import cn.felord.spring.security.exception.SimpleAccessDeniedHandler;
import cn.felord.spring.security.exception.SimpleAuthenticationEntryPoint;
import cn.felord.spring.security.filter.JsonLoginPostProcessor;
import cn.felord.spring.security.filter.JwtAuthenticationFilter;
import cn.felord.spring.security.filter.LoginPostProcessor;
import cn.felord.spring.security.filter.PreLoginFilter;
import cn.felord.spring.security.handler.CustomLogoutHandler;
import cn.felord.spring.security.handler.CustomLogoutSuccessHandler;
import cn.felord.spring.security.jwt.JwtTokenGenerator;
import cn.felord.spring.security.jwt.JwtTokenStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Collections;

/**
 * CustomSpring
 *
 * @author Felordcn
 * @see org.springframework.boot.autoconfigure.security.servlet.SpringBootWebSecurityConfiguration
 * @since 14 :58 2019/10/15
 */
@Configuration
@ConditionalOnClass(WebSecurityConfigurerAdapter.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CustomSpringBootWebSecurityConfiguration {
    private static final String LOGIN_PROCESSING_URL = "/process";

    /**
     * Json login post processor json login post processor.
     *
     * @return the json login post processor
     */
    @Bean
    public JsonLoginPostProcessor jsonLoginPostProcessor() {
        return new JsonLoginPostProcessor();
    }

    /**
     * Pre login filter pre login filter.
     *
     * @param loginPostProcessors the login post processors
     * @return the pre login filter
     */
    @Bean
    public PreLoginFilter preLoginFilter(Collection<LoginPostProcessor> loginPostProcessors) {
        return new PreLoginFilter(LOGIN_PROCESSING_URL, loginPostProcessors);
    }

    /**
     * Jwt 认证过滤器.
     *
     * @param jwtTokenGenerator jwt 工具类 负责 生成 验证 解析
     * @param jwtTokenStorage   jwt 缓存存储接口
     * @return the jwt authentication filter
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenGenerator jwtTokenGenerator, JwtTokenStorage jwtTokenStorage) {
        return new JwtAuthenticationFilter(jwtTokenGenerator, jwtTokenStorage);
    }

    /**
     * The type Default configurer adapter.
     */
    @Configuration
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    static class DefaultConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        private JwtAuthenticationFilter jwtAuthenticationFilter;
        @Autowired
        private PreLoginFilter preLoginFilter;
        @Autowired
        private AuthenticationSuccessHandler authenticationSuccessHandler;
        @Autowired
        private AuthenticationFailureHandler authenticationFailureHandler;
        @Autowired
        private FilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource;
        @Autowired
        private AccessDecisionManager accessDecisionManager;
        @Autowired
        CaptchaService captchaService;


        /**
         * 验证码认证器.
         *
         * @return the captcha authentication provider
         */
        public CaptchaAuthenticationProvider captchaAuthenticationProvider() {
            //TODO 如果是多个 UserDetailService  需要做成复合Bean注入Spring IoC   也就是 一个配置中不能直接有两个 UserDetailService
            // 切记切记
            return new CaptchaAuthenticationProvider(username -> User.withUsername(username).password("TEMP")
                    //todo  这里权限 你需要自己注入
                    .authorities(AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_APP")).build(), captchaService);
        }


        /**
         * 验证码认证过滤器.
         *
         * @return the captcha authentication filter
         */

        @Bean
        public CaptchaAuthenticationFilter captchaAuthenticationFilter(){
            CaptchaAuthenticationFilter captchaAuthenticationFilter = new CaptchaAuthenticationFilter();
            // 配置 authenticationManager
            ProviderManager providerManager = new ProviderManager(Collections.singletonList(captchaAuthenticationProvider()));
            captchaAuthenticationFilter.setAuthenticationManager(providerManager);
            // 成功处理器
            captchaAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
            // 失败处理器
            captchaAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

            return captchaAuthenticationFilter;
        }


        @Override
        public void configure(WebSecurity web) throws Exception {
            super.configure(web);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            ApplicationContext context = http.getSharedObject(ApplicationContext.class);
            CaptchaAuthenticationFilter captchaAuthenticationFilter = context.getBean(CaptchaAuthenticationFilter.class);
            http.csrf().disable()
                    .cors()
                    .and()
                    // session 生成策略用无状态策略
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .exceptionHandling().accessDeniedHandler(new SimpleAccessDeniedHandler()).authenticationEntryPoint(new SimpleAuthenticationEntryPoint())
                    .and()
                    .authorizeRequests().anyRequest().authenticated().withObjectPostProcessor(filterSecurityInterceptorObjectPostProcessor())
                    .and()
                    .addFilterBefore(preLoginFilter, UsernamePasswordAuthenticationFilter.class)
                    // 验证码登陆过滤器配置
                    .addFilterBefore(captchaAuthenticationFilter, PreLoginFilter.class)
                    // jwt 必须配置于 UsernamePasswordAuthenticationFilter 之前
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    // 登录  成功后返回jwt token  失败后返回 错误信息
                    .formLogin().loginProcessingUrl(LOGIN_PROCESSING_URL).successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler)
                    .and().logout().addLogoutHandler(new CustomLogoutHandler()).logoutSuccessHandler(new CustomLogoutSuccessHandler());

        }

        /**
         * 自定义 FilterSecurityInterceptor  ObjectPostProcessor 以替换默认配置达到动态权限的目的
         *
         * @return ObjectPostProcessor
         */
        private ObjectPostProcessor<FilterSecurityInterceptor> filterSecurityInterceptorObjectPostProcessor() {
            return new ObjectPostProcessor<FilterSecurityInterceptor>() {
                @Override
                public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                    object.setAccessDecisionManager(accessDecisionManager);
                    object.setSecurityMetadataSource(filterInvocationSecurityMetadataSource);
                    return object;
                }
            };
        }

    }
}
