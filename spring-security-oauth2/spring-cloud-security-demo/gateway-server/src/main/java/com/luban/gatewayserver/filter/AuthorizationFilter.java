package com.luban.gatewayserver.filter;

import com.luban.gatewayserver.common.TokenInfo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Fox
 */
@Component
@Order(1)
public class AuthorizationFilter implements GlobalFilter, InitializingBean {


    private static Set<String> shouldSkipUrl = new LinkedHashSet<>();
    @Override
    public void afterPropertiesSet() throws Exception {
        // 不拦截认证的请求
        shouldSkipUrl.add("/oauth/token");
        shouldSkipUrl.add("/oauth/check_token");
        shouldSkipUrl.add("/user/getCurrentUser");

    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String requestPath = exchange.getRequest().getURI().getPath();
        //不需要认证的url
        if(shouldSkip(requestPath)) {
            return chain.filter(exchange);
        }
        TokenInfo tokenInfo = exchange.getAttribute("tokenInfo");

        if(!tokenInfo.isActive()) {
            throw new RuntimeException("token过期");
        }

        hasPremisson(tokenInfo,requestPath);

        return chain.filter(exchange);
    }

    private boolean shouldSkip(String reqPath) {

        for(String skipPath:shouldSkipUrl) {
            if(reqPath.contains(skipPath)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasPremisson(TokenInfo tokenInfo,String currentUrl) {
        boolean hasPremisson = false;
        //登录用户的权限集合判断
        List<String> premessionList = Arrays.asList(tokenInfo.getAuthorities());
        for (String url: premessionList) {
            if(currentUrl.contains(url)) {
                hasPremisson = true;
                break;
            }
        }
        if(!hasPremisson){
            throw new RuntimeException("没有权限");
        }

        return hasPremisson;
    }


}
