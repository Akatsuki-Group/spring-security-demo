package cn.felord.spring.security.config;

import cn.felord.spring.security.jwt.JwtProperties;
import cn.felord.spring.security.jwt.JwtTokenCacheStorage;
import cn.felord.spring.security.jwt.JwtTokenGenerator;
import cn.felord.spring.security.jwt.JwtTokenStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JwtConfiguration
 *
 * @author Felordcn
 * @since 16 :54 2019/10/25
 */
@EnableConfigurationProperties(JwtProperties.class)
@ConditionalOnProperty(prefix = "jwt.config",name = "enabled")
@Configuration
public class JwtConfiguration {


    /**
     * Jwt token storage .
     *
     * @return the jwt token storage
     */
    @Bean
    public JwtTokenStorage jwtTokenStorage() {
        return new JwtTokenCacheStorage();
    }


    /**
     * Jwt token generator.
     *
     * @param jwtTokenStorage the jwt token storage
     * @param jwtProperties   the jwt properties
     * @return the jwt token generator
     */
    @Bean
    public JwtTokenGenerator jwtTokenGenerator(JwtTokenStorage jwtTokenStorage, JwtProperties jwtProperties) {
        return new JwtTokenGenerator(jwtTokenStorage, jwtProperties);
    }

}
