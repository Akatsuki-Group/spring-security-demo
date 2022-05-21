package cn.felord.spring.security.dynamic;

import cn.felord.spring.security.service.MetaResourceService;
import cn.felord.spring.security.service.RoleService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 参考 ExpressionBasedFilterInvocationSecurityMetadataSource
 *
 * @author Felordcn
 * @since 14:27 2019/11/27
 **/

public class DynamicFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
    @Resource
    private RequestMatcherCreator requestMatcherCreator;
    @Resource
    private MetaResourceService metaResourceService;
    @Resource
    private RoleService roleService;

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {

        // 获取当前的请求
        final HttpServletRequest request = ((FilterInvocation) object).getRequest();
        // 先验证是不是白名单接口
        // 这里可以实现一个白名单接口来处理 ROLE_ANONYMOUS 匿名访问的接口 演示代码就静态处理了
        final Set<RequestMatcher> ANONYMOUS = new HashSet<>();
        ANONYMOUS.add(new AntPathRequestMatcher("/clogin", "POST"));
        ANONYMOUS.add(new AntPathRequestMatcher("/captcha/*", "GET"));
        ANONYMOUS.add(new AntPathRequestMatcher("/favicon.ico", "GET"));

        final Optional<RequestMatcher> any = ANONYMOUS.stream()
                .filter(requestMatcher -> requestMatcher.matches(request))
                .findAny();

        // 如果在白名单 则直接返回
        if (any.isPresent()) {
            return SecurityConfig.createList(ROLE_ANONYMOUS);
        }

        // 这里可以放一个抽象接口来获取  request 配置的 ant pattern
        Set<RequestMatcher> requestMatchers = requestMatcherCreator.convertToRequestMatcher(metaResourceService.queryPatternsAndMethods());
        // 拿到其中一个  没有就算非法访问
        RequestMatcher reqMatcher = requestMatchers.stream()
                .filter(requestMatcher -> requestMatcher.matches(request))
                .findAny()
                .orElseThrow(() -> new AccessDeniedException("非法访问"));

        AntPathRequestMatcher antPathRequestMatcher = (AntPathRequestMatcher) reqMatcher;
        // 根据pattern 获取该pattern被授权给的角色
        String pattern = antPathRequestMatcher.getPattern();
        Set<String> roles = roleService.queryRoleByPattern(pattern);

        return CollectionUtils.isEmpty(roles) ? null : SecurityConfig.createList(roles.toArray(new String[0]));
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<String> roles = roleService.queryAllAvailable();
        return CollectionUtils.isEmpty(roles) ? null : SecurityConfig.createList(roles.toArray(new String[0]));
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
