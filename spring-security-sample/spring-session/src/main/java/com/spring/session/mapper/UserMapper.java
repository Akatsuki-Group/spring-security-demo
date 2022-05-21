package com.spring.session.mapper;

import com.spring.session.bean.UserDto;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author security
 */
@Repository
public interface UserMapper {


    @Select("select * from t_user where username=#{username}")
    public UserDto getUserByUsername(String username);
}
