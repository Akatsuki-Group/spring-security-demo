package cn.felord.spring.security.oauth2;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import sun.security.provider.certpath.Builder;

/**
 * @author tian
 * @see org.springframework.security.config.oauth2.client.CommonOAuth2Provider
 */
public enum CustomOAuth2Provider {
    /**
     * gitee
     */
    GITEE {
        @Override
        public Builder getBuilder(String registrationId) {
            return null;
        }
    };


    private CustomOAuth2Provider() {
    }

    protected final ClientRegistration.Builder getBuilder(String registrationId, ClientAuthenticationMethod method, String redirectUri) {
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId);
        builder.clientAuthenticationMethod(method);
        builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
        builder.redirectUriTemplate(redirectUri);
        return builder;
    }

    public abstract Builder getBuilder(String registrationId);
}
