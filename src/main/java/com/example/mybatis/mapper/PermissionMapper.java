package com.example.mybatis.mapper;

import com.example.mybatis.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionMapper {

    Permission selectById(@Param("id") Long id);

    List<Permission> selectByUserId(@Param("userId") Long userId);

    List<Permission> selectByCondition(
            @Param("code") String code,
            @Param("name") String name,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    long countByCondition(@Param("code") String code, @Param("name") String name);

    int insert(Permission permission);

    int update(Permission permission);

    int deleteById(@Param("id") Long id);
}
