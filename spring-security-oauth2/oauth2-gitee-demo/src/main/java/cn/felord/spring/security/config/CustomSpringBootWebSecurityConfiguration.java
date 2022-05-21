package cn.felord.spring.security.config;

import cn.felord.spring.security.captcha.CaptchaAuthenticationFilter;
import cn.felord.spring.security.entity.RestBody;
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
import cn.felord.spring.security.oauth2.CustomOAuth2AuthorizationCodeGrantRequestEntityConverter;
import cn.felord.spring.security.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        private CaptchaAuthenticationFilter captchaAuthenticationFilter;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            super.configure(auth);
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            super.configure(web);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
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
                    .oauth2Login().userInfoEndpoint(userInfoEndpointConfig -> {
                DefaultOAuth2UserService userService = new DefaultOAuth2UserService();

                userService.setRequestEntityConverter(userRequest -> {
                    ClientRegistration clientRegistration = userRequest.getClientRegistration();

                    HttpMethod httpMethod = HttpMethod.GET;

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                    headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36");
                    MultiValueMap<String, String> formParameters = new LinkedMultiValueMap<>();
                    formParameters.add(OAuth2ParameterNames.ACCESS_TOKEN, userRequest.getAccessToken().getTokenValue());
                    URI uri = UriComponentsBuilder.fromUriString(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri())
                            .queryParams(formParameters)
                            .build()
                            .toUri();

                    return new RequestEntity<>(headers, httpMethod, uri);

                });


                userInfoEndpointConfig.userService(userService);
            })

                    .tokenEndpoint()
                    .accessTokenResponseClient(auth2AccessTokenResponseClient())
                    .and()
                    .successHandler((request, response, authentication) -> {
                        Map<String, Object> map = new HashMap<>(5);
                        map.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        map.put("authentication", authentication);
                        map.put("flag", "oauth_login");

                        ResponseUtil.responseJsonWriter(response, RestBody.okData(map, "登录成功"));
                    })
                    .and()
                    // 登录  成功后返回jwt token  失败后返回 错误信息
                    .formLogin().loginProcessingUrl(LOGIN_PROCESSING_URL)
                    .successHandler(authenticationSuccessHandler)
                    .failureHandler(authenticationFailureHandler)
                    .and().logout().addLogoutHandler(new CustomLogoutHandler()).logoutSuccessHandler(new CustomLogoutSuccessHandler());

        }

        private DefaultAuthorizationCodeTokenResponseClient auth2AccessTokenResponseClient() {
            DefaultAuthorizationCodeTokenResponseClient client = new DefaultAuthorizationCodeTokenResponseClient();
            client.setRequestEntityConverter(new CustomOAuth2AuthorizationCodeGrantRequestEntityConverter());
            return client;
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
