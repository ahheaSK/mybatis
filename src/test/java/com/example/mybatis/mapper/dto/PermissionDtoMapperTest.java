package com.example.mybatis.mapper.dto;

import com.example.mybatis.dto.request.PermissionCreateRequest;
import com.example.mybatis.dto.request.PermissionUpdateRequest;
import com.example.mybatis.dto.response.PermissionResponse;
import com.example.mybatis.entity.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for MapStruct-generated {@link PermissionDtoMapperImpl}.
 */
class PermissionDtoMapperTest {

    private PermissionDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PermissionDtoMapperImpl();
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
            PermissionCreateRequest request = new PermissionCreateRequest("READ", "Read access", "Can read");
            Permission entity = mapper.toEntity(request);
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isNull();
            assertThat(entity.getCode()).isEqualTo("READ");
            assertThat(entity.getName()).isEqualTo("Read access");
            assertThat(entity.getDescription()).isEqualTo("Can read");
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
            Permission entity = new Permission(10L, "WRITE", "Write access", "Can write", null);
            PermissionResponse dto = mapper.toDTO(entity);
            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(10L);
            assertThat(dto.getCode()).isEqualTo("WRITE");
            assertThat(dto.getName()).isEqualTo("Write access");
            assertThat(dto.getDescription()).isEqualTo("Can write");
        }
    }

    @Nested
    @DisplayName("updateEntity")
    class UpdateEntity {
        @Test
        @DisplayName("does nothing when request is null")
        void nullRequest() {
            Permission target = new Permission(1L, "OLD", "Old", null, null);
            mapper.updateEntity(target, null);
            assertThat(target.getCode()).isEqualTo("OLD");
            assertThat(target.getName()).isEqualTo("Old");
        }

        @Test
        @DisplayName("updates only non-null fields")
        void partialUpdate() {
            Permission target = new Permission(1L, "OLD", "Old Name", "Old desc", null);
            PermissionUpdateRequest request = new PermissionUpdateRequest("NEW_CODE", null, null);
            mapper.updateEntity(target, request);
            assertThat(target.getCode()).isEqualTo("NEW_CODE");
            assertThat(target.getName()).isEqualTo("Old Name");
            assertThat(target.getDescription()).isEqualTo("Old desc");
        }

        @Test
        @DisplayName("updates all fields when all set")
        void fullUpdate() {
            Permission target = new Permission(1L, "A", "B", "C", null);
            PermissionUpdateRequest request = new PermissionUpdateRequest("X", "Y", "Z");
            mapper.updateEntity(target, request);
            assertThat(target.getCode()).isEqualTo("X");
            assertThat(target.getName()).isEqualTo("Y");
            assertThat(target.getDescription()).isEqualTo("Z");
            assertThat(target.getId()).isEqualTo(1L);
        }
    }
}
