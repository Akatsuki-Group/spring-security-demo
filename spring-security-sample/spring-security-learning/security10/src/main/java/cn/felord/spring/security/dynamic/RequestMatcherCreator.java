package cn.felord.spring.security.dynamic;

import cn.felord.spring.security.entity.MetaResource;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Set;
import java.util.function.Supplier;

/**
 * RequestMatcherRepository
 *
 * @author Felordcn
 * @since 14 :56 2019/11/28
 */
public interface RequestMatcherCreator {

    /**
     * 转换为 reqMatcher
     *
     * @param metaResources metaResource
     * @return  reqMatcher
     */
    Set<RequestMatcher> convertToRequestMatcher(Set<MetaResource> metaResources);


}
