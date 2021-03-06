package cn.felord.spring.security.config;

import cn.felord.spring.security.dynamic.DynamicFilterInvocationSecurityMetadataSource;
import cn.felord.spring.security.dynamic.RequestMatcherCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 动态权限组件配置
 *
 * @author Felordcn
 */
@Configuration
public class DynamicAccessControlConfiguration {
    /**
     * RequestMatcher 生成器
     *
     * @return RequestMatcher
     */
    @Bean
    public RequestMatcherCreator requestMatcherCreator() {
        return metaResources -> metaResources.stream()
                .map(metaResource -> new AntPathRequestMatcher(metaResource.getPattern(), metaResource.getMethod()))
                .collect(Collectors.toSet());
    }

    /**
     * 元数据加载器
     *
     * @return dynamicFilterInvocationSecurityMetadataSource
     */
    @Bean
    public FilterInvocationSecurityMetadataSource dynamicFilterInvocationSecurityMetadataSource() {
        return new DynamicFilterInvocationSecurityMetadataSource();
    }

    /**
     * 角色投票器
     *
     * @return roleVoter
     */
    @Bean
    public RoleVoter roleVoter() {
        return new RoleVoter();
    }

    /**
     * 基于肯定的访问决策器
     *
     * @param decisionVoters AccessDecisionVoter类型的 Bean 会自动注入到 decisionVoters
     * @return affirmativeBased
     */
    @Bean
    public AccessDecisionManager affirmativeBased(List<AccessDecisionVoter<?>> decisionVoters) {
        return new AffirmativeBased(decisionVoters);
    }

}
