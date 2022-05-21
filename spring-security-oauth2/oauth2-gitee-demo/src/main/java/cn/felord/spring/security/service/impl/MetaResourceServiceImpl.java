package cn.felord.spring.security.service.impl;

import cn.felord.spring.security.entity.MetaResource;
import cn.felord.spring.security.service.MetaResourceService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
@Service
public class MetaResourceServiceImpl implements MetaResourceService {
    @Override
    public Set<MetaResource> queryPatternsAndMethods() {

        Set<MetaResource> metaResources = new HashSet<>();

        MetaResource e = new MetaResource();
        e.setPattern("/foo/param/*");
        e.setMethod("GET");
        metaResources.add(e);
        MetaResource m = new MetaResource();
        m.setPattern("/foo/prefilter");
        m.setMethod("POST");
        metaResources.add(m);

        return metaResources;
    }
}
