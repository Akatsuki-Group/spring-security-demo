package cn.felord.spring.security.config;

import cn.felord.spring.security.oauth2.CustomOAuth2Provider;
import cn.felord.spring.security.oauth2.DelegateOAuth2AuthorizationCodeGrantRequestEntityConverter;
import cn.felord.spring.security.oauth2.DelegateOAuth2UserRequestEntityConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type O auth 2 provider configuration.
 *
 * @author Dax
 * @since 11 :26
 */
@Slf4j
@Configuration
public class OAuth2ProviderConfiguration {


    /**
     * Delegate o auth 2 user request entity converter delegate o auth 2 user request entity converter.
     *
     * @return the delegate o auth 2 user request entity converter
     */
    @Bean
    public DelegateOAuth2UserRequestEntityConverter delegateOAuth2UserRequestEntityConverter() {
        DelegateOAuth2UserRequestEntityConverter delegateOAuth2UserRequestEntityConverter = new DelegateOAuth2UserRequestEntityConverter();
        Arrays.stream(CustomOAuth2Provider.values()).forEach(customOAuth2Provider -> {

            String registrationId = customOAuth2Provider.getRegistrationId();
            Converter<OAuth2UserRequest, RequestEntity<?>> userRequestEntityConverter = customOAuth2Provider.userInfoUriRequestEntityConverter();

            delegateOAuth2UserRequestEntityConverter.addConverter(registrationId, userRequestEntityConverter);

        });
        return delegateOAuth2UserRequestEntityConverter;
    }


    /**
     * 后续进行数据库持久化更加科学.
     *
     * @param properties the properties
     * @return the in memory client registration repository
     */
    @Bean
    InMemoryClientRegistrationRepository clientRegistrationRepository(OAuth2ClientProperties properties) {
        Set<String> registrationIds = Arrays.stream(CustomOAuth2Provider.values())
                .map(CustomOAuth2Provider::getRegistrationId)
                .collect(Collectors.toSet());
        OAuth2ClientProperties oAuth2ClientProperties = new OAuth2ClientProperties();

        properties.getRegistration().keySet().forEach(registrationId -> {
            if (!registrationIds.contains(registrationId)) {
                oAuth2ClientProperties.getRegistration().put(registrationId, properties.getRegistration().get(registrationId));
                oAuth2ClientProperties.getProvider().put(registrationId, properties.getProvider().get(registrationId));

            }
        });
        List<ClientRegistration> clientRegistrationList = new ArrayList<>(
                OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(oAuth2ClientProperties).values());

        List<ClientRegistration> registrations = Arrays.stream(CustomOAuth2Provider.values())
                .map(customOAuth2Provider -> {
                    try {
                        return customOAuth2Provider.getBuilder(properties);
                    } catch (Exception e) {
                        // 如果配置不完整将不被注册，忽略掉
                        log.warn(" ClientRegistration @ {} is not registered , result {}",
                                customOAuth2Provider.getRegistrationId(),
                                e.getMessage());
                        return null;
                    }
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());

        registrations.addAll(clientRegistrationList);
        return new InMemoryClientRegistrationRepository(registrations);
    }


    /**
     * 委托转换器以满足个性化需求.
     *
     * @return the delegate o auth 2 authorization code grant request entity converter
     */
    @Bean
    public DelegateOAuth2AuthorizationCodeGrantRequestEntityConverter oAuth2AuthorizationCodeGrantRequestEntityConverter() {

        DelegateOAuth2AuthorizationCodeGrantRequestEntityConverter converter = new DelegateOAuth2AuthorizationCodeGrantRequestEntityConverter();
        Arrays.stream(CustomOAuth2Provider.values()).forEach(customOAuth2Provider -> {

            String registrationId = customOAuth2Provider.getRegistrationId();
            Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> grantRequestRequestEntityConverter = customOAuth2Provider.tokenUriRequestEntityConverter();

            converter.addConverter(registrationId, grantRequestRequestEntityConverter);

        });
        return converter;
    }


    /**
     * O auth 2 user service default o auth 2 user service.
     *
     * @param converter the converter
     * @return the default o auth 2 user service
     */
    @Bean
    public DefaultOAuth2UserService oAuth2UserService(DelegateOAuth2UserRequestEntityConverter converter) {
        DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();
        defaultOAuth2UserService.setRequestEntityConverter(converter);
        return defaultOAuth2UserService;
    }

    /**
     * Spring Security 并没有提供默认的自动注入入口 可惜 需要手动在配置中添加.
     *
     * @param converter the converter
     * @return the default authorization code token response client
     */
    @Bean
    public DefaultAuthorizationCodeTokenResponseClient oAuth2AccessTokenResponseClient(DelegateOAuth2AuthorizationCodeGrantRequestEntityConverter converter) {
        DefaultAuthorizationCodeTokenResponseClient defaultAuthorizationCodeTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        defaultAuthorizationCodeTokenResponseClient.setRequestEntityConverter(converter);
        return defaultAuthorizationCodeTokenResponseClient;
    }
}
