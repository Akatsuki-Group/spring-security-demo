package com.spring.security.mapper;


import com.spring.security.bean.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author security
 */
@Repository
public interface UserMapper {

    @Select("select * from tb_user where username=#{username}")
    User getByUsername(String username);
}
