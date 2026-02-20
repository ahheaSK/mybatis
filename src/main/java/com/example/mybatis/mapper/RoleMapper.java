package com.example.mybatis.mapper;

import com.example.mybatis.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {

    Role selectById(@Param("id") Long id);

    List<Role> selectByCondition(
            @Param("code") String code,
            @Param("name") String name,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    long countByCondition(@Param("code") String code, @Param("name") String name);

    List<Role> selectByUserId(@Param("userId") Long userId);

    /** Returns role ids that exist in DB (and not soft-deleted). */
    List<Long> selectExistingIds(@Param("ids") List<Long> ids);

    int insert(Role role);

    int update(Role role);

    int deleteById(@Param("id") Long id);
}
