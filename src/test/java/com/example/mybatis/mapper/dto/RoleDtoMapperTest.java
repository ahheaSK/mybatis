package com.example.mybatis.mapper.dto;

import com.example.mybatis.dto.request.RoleCreateRequest;
import com.example.mybatis.dto.request.RoleUpdateRequest;
import com.example.mybatis.dto.response.RoleResponse;
import com.example.mybatis.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for MapStruct-generated {@link RoleDtoMapperImpl}.
 */
class RoleDtoMapperTest {

    private RoleDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RoleDtoMapperImpl();
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {
        @Test
        @DisplayName("returns null when request is null")
        void nullRequest() {
            assertThat(mapper.toEntity(null)).isNull();
        }

        @Test
        @DisplayName("maps create request to entity without id")
        void success() {
            RoleCreateRequest request = new RoleCreateRequest("ADMIN", "Administrator", "Admin role");
            Role entity = mapper.toEntity(request);
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isNull();
            assertThat(entity.getCode()).isEqualTo("ADMIN");
            assertThat(entity.getName()).isEqualTo("Administrator");
            assertThat(entity.getDescription()).isEqualTo("Admin role");
        }
    }

    @Nested
    @DisplayName("toDTO")
    class ToDTO {
        @Test
        @DisplayName("returns null when entity is null")
        void nullEntity() {
            assertThat(mapper.toDTO(null)).isNull();
        }

        @Test
        @DisplayName("maps entity to response")
        void success() {
            Role entity = new Role(1L, "USER", "User", "Regular user", null);
            RoleResponse dto = mapper.toDTO(entity);
            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getCode()).isEqualTo("USER");
            assertThat(dto.getName()).isEqualTo("User");
            assertThat(dto.getDescription()).isEqualTo("Regular user");
        }
    }

    @Nested
    @DisplayName("updateEntity")
    class UpdateEntity {
        @Test
        @DisplayName("does nothing when request is null")
        void nullRequest() {
            Role target = new Role(1L, "OLD", "Old", null, null);
            mapper.updateEntity(target, null);
            assertThat(target.getCode()).isEqualTo("OLD");
            assertThat(target.getName()).isEqualTo("Old");
        }

        @Test
        @DisplayName("updates only non-null fields")
        void partialUpdate() {
            Role target = new Role(1L, "OLD", "Old Name", "Old desc", null);
            RoleUpdateRequest request = new RoleUpdateRequest("NEW_CODE", null, null);
            mapper.updateEntity(target, request);
            assertThat(target.getCode()).isEqualTo("NEW_CODE");
            assertThat(target.getName()).isEqualTo("Old Name");
            assertThat(target.getDescription()).isEqualTo("Old desc");
        }

        @Test
        @DisplayName("updates all fields when all set")
        void fullUpdate() {
            Role target = new Role(1L, "A", "B", "C", null);
            RoleUpdateRequest request = new RoleUpdateRequest("X", "Y", "Z");
            mapper.updateEntity(target, request);
            assertThat(target.getCode()).isEqualTo("X");
            assertThat(target.getName()).isEqualTo("Y");
            assertThat(target.getDescription()).isEqualTo("Z");
            assertThat(target.getId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("toDTOList")
    class ToDTOList {
        @Test
        @DisplayName("returns null when list is null")
        void nullList() {
            assertThat(mapper.toDTOList(null)).isNull();
        }

        @Test
        @DisplayName("maps list of entities to list of DTOs")
        void success() {
            List<Role> entities = List.of(
                    new Role(1L, "A", "Role A", null, null),
                    new Role(2L, "B", "Role B", "desc", null));
            List<RoleResponse> list = mapper.toDTOList(entities);
            assertThat(list).hasSize(2);
            assertThat(list.get(0).getId()).isEqualTo(1L);
            assertThat(list.get(0).getCode()).isEqualTo("A");
            assertThat(list.get(1).getId()).isEqualTo(2L);
            assertThat(list.get(1).getCode()).isEqualTo("B");
        }
    }
}
