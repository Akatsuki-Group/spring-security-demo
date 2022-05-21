package cn.felord.spring.security.service.impl;

import cn.felord.spring.security.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
@Service
public class RoleServiceImpl implements RoleService {
    @Override
    public Set<String> queryRoleByPattern(String pattern) {
        //todo  写你这里的逻辑  这里为了演示方便不使用数据库 这里遵循 ROLE_前缀
        //todo 你可以通过修改集合来模拟动态权限控制

        Set<String> roles = new HashSet<>();

        roles.add("ROLE_ADMIN1");
        // ROLE_ANONYMOUS 表示可以匿名访问  如果你对某个接口使用了该角色则表明 该接口为开放性接口
        roles.add("ROLE_APP1");
        return roles;
    }

    @Override
    public Set<String> queryAllAvailable() {
        Set<String> roles = new HashSet<>();

        roles.add("ROLE_ADMIN");
        roles.add("ROLE_APP");
        roles.add("ROLE_LEADER");
        roles.add("ROLE_ROOT");

        return roles;
    }
}
