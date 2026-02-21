package com.example.mybatis.mapper;

import com.example.mybatis.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper {

    Menu selectById(@Param("id") Long id);

    List<Menu> selectByCondition(
            @Param("name") String name,
            @Param("path") String path,
            @Param("parentId") Long parentId,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    long countByCondition(
            @Param("name") String name,
            @Param("path") String path,
            @Param("parentId") Long parentId
    );

    /** Returns menu ids that exist in DB (and not soft-deleted). */
    List<Long> selectExistingIds(@Param("ids") List<Long> ids);

    int insert(Menu menu);

    int update(Menu menu);

    int deleteById(@Param("id") Long id, @Param("username") String username);
}
