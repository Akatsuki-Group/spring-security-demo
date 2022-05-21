package cn.felord.spring.security.oauth2;

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

/**
 * The enum Custom o auth 2 provider.
 *
 * @author Dax
 * @since 9 :50
 */
public enum CustomOAuth2Provider {
    /**
     * The Gitee.
     */
    GITEE {
        @Override
        public ClientRegistration getBuilder(OAuth2ClientProperties properties) {

            String registrationId = getRegistrationId();
            ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId);
            OAuth2ClientProperties.Registration registration = properties.getRegistration().get(registrationId);

            builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
            builder.redirectUriTemplate(DEFAULT_REDIRECT_URL);

            builder.clientId(registration.getClientId());
            builder.clientSecret(registration.getClientSecret());
            builder.scope("user_info");
            builder.authorizationUri("https://gitee.com/oauth/authorize");
            builder.tokenUri("https://gitee.com/oauth/token");
            builder.userInfoUri("https://gitee.com/api/v5/user");
            builder.userNameAttributeName("name");
            builder.clientName("Gitee");
            // 配置优先原则
            return fromProperties(registrationId, properties, builder);
        }

        @Override
        public String getRegistrationId() {
            return "gitee";
        }

        @Override
        public Converter<OAuth2UserRequest, RequestEntity<?>> userInfoUriRequestEntityConverter() {
            return userRequest -> {
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

            };
        }

        @Override
        public Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> tokenUriRequestEntityConverter() {
            return new GiteeOAuth2AuthorizationCodeGrantRequestEntityConverter();
        }
    };

    private static final String DEFAULT_REDIRECT_URL = "{baseUrl}/{action}/oauth2/code/{registrationId}";
    private static final Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> TOKEN_REQUEST_ENTITY_CONVERTER = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
    private static final Converter<OAuth2UserRequest, RequestEntity<?>> USER_INFO_REQUEST_ENTITY_CONVERTER = new OAuth2UserRequestEntityConverter();

    CustomOAuth2Provider() {
    }


    /**
     * Gets builder.
     *
     * @param properties the properties
     * @return the builder
     */
    public abstract ClientRegistration getBuilder(OAuth2ClientProperties properties);

    /**
     * Gets registration id.
     *
     * @return the registration id
     */
    public abstract String getRegistrationId();

    /**
     * 请求 user_info_uri 定制请求参数.
     *
     * @return the converter
     */
    public abstract Converter<OAuth2UserRequest, RequestEntity<?>> userInfoUriRequestEntityConverter();

    /**
     * 请求 token-uri 定制请求参数.
     *
     * @return the converter
     */
    public abstract Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> tokenUriRequestEntityConverter();


    /**
     * 从配置文件中加载配置，优先级最高.
     *
     * @param registrationId the registration id
     * @param properties     the properties
     * @param builder        the builder
     * @return the client registration
     */
    private static ClientRegistration fromProperties(String registrationId, OAuth2ClientProperties properties, ClientRegistration.Builder builder) {
        if (Objects.isNull(properties)) {
            return builder.build();
        }

        OAuth2ClientProperties.Registration registration = properties.getRegistration().get(registrationId);
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();

        if (Objects.nonNull(registration)) {
            map.from(registration::getClientId).to(builder::clientId);
            map.from(registration::getClientSecret).to(builder::clientSecret);
            map.from(registration::getClientAuthenticationMethod).as(ClientAuthenticationMethod::new)
                    .to(builder::clientAuthenticationMethod);
            map.from(registration::getAuthorizationGrantType).as(AuthorizationGrantType::new)
                    .to(builder::authorizationGrantType);
            map.from(registration::getRedirectUri).to(builder::redirectUriTemplate);
            map.from(registration::getScope).as(StringUtils::toStringArray).to(builder::scope);
            map.from(registration::getClientName).to(builder::clientName);
        }

        OAuth2ClientProperties.Provider provider = properties.getProvider().get(registrationId);

        if (Objects.nonNull(provider)) {
            map.from(provider::getAuthorizationUri).to(builder::authorizationUri);
            map.from(provider::getTokenUri).to(builder::tokenUri);
            map.from(provider::getUserInfoUri).to(builder::userInfoUri);
            map.from(provider::getUserInfoAuthenticationMethod).as(AuthenticationMethod::new)
                    .to(builder::userInfoAuthenticationMethod);
            map.from(provider::getJwkSetUri).to(builder::jwkSetUri);
            map.from(provider::getUserNameAttribute).to(builder::userNameAttributeName);
        }

        return builder.build();
    }


}
