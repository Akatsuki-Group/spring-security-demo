package com.spring.security.mapper;

import com.spring.security.bean.Permission;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author security
 */
@Repository
public interface PermissionMapper  {

    @Select("SELECT\n" +
            "  p.*\n" +
            "FROM\n" +
            "  tb_user AS u\n" +
            "  LEFT JOIN tb_user_role AS ur\n" +
            "    ON u.id = ur.user_id\n" +
            "  LEFT JOIN tb_role AS r\n" +
            "    ON r.id = ur.role_id\n" +
            "  LEFT JOIN tb_role_permission AS rp\n" +
            "    ON r.id = rp.role_id\n" +
            "  LEFT JOIN tb_permission AS p\n" +
            "    ON p.id = rp.permission_id\n" +
            "WHERE u.id = #{userId}")
    List<Permission> selectByUserId(Long userId);
}
