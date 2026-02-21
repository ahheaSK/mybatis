package com.example.mybatis.mapper;

import com.example.mybatis.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMenuMapper {

    int insert(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

    int deleteByRoleId(@Param("roleId") Long roleId);

    List<Menu> selectMenusByRoleId(@Param("roleId") Long roleId);
}
