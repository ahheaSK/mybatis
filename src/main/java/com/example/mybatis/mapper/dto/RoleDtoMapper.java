package com.example.mybatis.mapper.dto;

import com.example.mybatis.dto.request.RoleCreateRequest;
import com.example.mybatis.dto.request.RoleUpdateRequest;
import com.example.mybatis.dto.response.RoleResponse;
import com.example.mybatis.entity.Role;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleDtoMapper {

    @Mapping(target = "id", ignore = true)
    Role toEntity(RoleCreateRequest dto);

    RoleResponse toDTO(Role entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Role target, RoleUpdateRequest request);

    List<RoleResponse> toDTOList(List<Role> entities);
}
