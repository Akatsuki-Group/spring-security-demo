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
     * Jwt ???????????????.
     *
     * @param jwtTokenGenerator jwt ????????? ?????? ?????? ?????? ??????
     * @param jwtTokenStorage   jwt ??????????????????
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
         * ??????????????????.
         *
         * @return the captcha authentication provider
         */
        public CaptchaAuthenticationProvider captchaAuthenticationProvider() {
            //TODO ??????????????? UserDetailService  ??????????????????Bean??????Spring IoC   ????????? ???????????????????????????????????? UserDetailService
            // ????????????
            return new CaptchaAuthenticationProvider(username -> User.withUsername(username).password("TEMP")
                    //todo  ???????????? ?????????????????????
                    .authorities(AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_APP")).build(), captchaService);
        }


        /**
         * ????????????????????????.
         *
         * @return the captcha authentication filter
         */

        @Bean
        public CaptchaAuthenticationFilter captchaAuthenticationFilter(){
            CaptchaAuthenticationFilter captchaAuthenticationFilter = new CaptchaAuthenticationFilter();
            // ?????? authenticationManager
            ProviderManager providerManager = new ProviderManager(Collections.singletonList(captchaAuthenticationProvider()));
            captchaAuthenticationFilter.setAuthenticationManager(providerManager);
            // ???????????????
            captchaAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
            // ???????????????
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
                    // session ??????????????????????????????
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .exceptionHandling().accessDeniedHandler(new SimpleAccessDeniedHandler()).authenticationEntryPoint(new SimpleAuthenticationEntryPoint())
                    .and()
                    .authorizeRequests().anyRequest().authenticated().withObjectPostProcessor(filterSecurityInterceptorObjectPostProcessor())
                    .and()
                    .addFilterBefore(preLoginFilter, UsernamePasswordAuthenticationFilter.class)
                    // ??????????????????????????????
                    .addFilterBefore(captchaAuthenticationFilter, PreLoginFilter.class)
                    // jwt ??????????????? UsernamePasswordAuthenticationFilter ??????
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    // ??????  ???????????????jwt token  ??????????????? ????????????
                    .formLogin().loginProcessingUrl(LOGIN_PROCESSING_URL).successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler)
                    .and().logout().addLogoutHandler(new CustomLogoutHandler()).logoutSuccessHandler(new CustomLogoutSuccessHandler());

        }

        /**
         * ????????? FilterSecurityInterceptor  ObjectPostProcessor ????????????????????????????????????????????????
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
