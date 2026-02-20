package com.example.mybatis.mapper;

import com.example.mybatis.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    User selectById(@Param("id") Long id);

    User selectByUsername(@Param("username") String username);

    List<User> selectByCondition(
            @Param("name") String name,
            @Param("email") String email,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    long countByCondition(
            @Param("name") String name,
            @Param("email") String email
    );

    int insert(User user);

    int update(User user);

    int deleteById(@Param("id") Long id);
}
