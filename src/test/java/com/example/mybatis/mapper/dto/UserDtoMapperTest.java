package com.example.mybatis.mapper.dto;

import com.example.mybatis.dto.request.UserCreateRequest;
import com.example.mybatis.dto.request.UserUpdateRequest;
import com.example.mybatis.dto.response.UserResponse;
import com.example.mybatis.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for MapStruct-generated {@link UserDtoMapperImpl}.
 */
class UserDtoMapperTest {

    private UserDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserDtoMapperImpl();
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
        @DisplayName("maps create request to entity; id and timestamps null, enabled default true")
        void success() {
            UserCreateRequest request = new UserCreateRequest("jane", "secret", "jane@example.com", true, List.of(1L));
            User entity = mapper.toEntity(request);
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isNull();
            assertThat(entity.getUsername()).isEqualTo("jane");
            assertThat(entity.getPassword()).isEqualTo("secret");
            assertThat(entity.getEmail()).isEqualTo("jane@example.com");
            assertThat(entity.getEnabled()).isTrue();
            assertThat(entity.getCreatedAt()).isNull();
            assertThat(entity.getUpdatedAt()).isNull();
        }

        @Test
        @DisplayName("sets enabled true when null in request")
        void enabledDefaultTrue() {
            UserCreateRequest request = new UserCreateRequest("u", "p", "e@e.com", null, List.of(1L));
            User entity = mapper.toEntity(request);
            assertThat(entity.getEnabled()).isTrue();
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
        @DisplayName("maps entity to response; roles not set")
        void success() {
            Instant now = Instant.now();
            User entity = new User(1L, "admin", "enc", "admin@example.com", true, now, now, null);
            UserResponse dto = mapper.toDTO(entity);
            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getUsername()).isEqualTo("admin");
            assertThat(dto.getEmail()).isEqualTo("admin@example.com");
            assertThat(dto.getEnabled()).isTrue();
            assertThat(dto.getCreatedAt()).isEqualTo(now);
            assertThat(dto.getUpdatedAt()).isEqualTo(now);
            assertThat(dto.getRoles()).isNull();
        }
    }

    @Nested
    @DisplayName("updateEntity")
    class UpdateEntity {
        @Test
        @DisplayName("does nothing when request is null")
        void nullRequest() {
            User target = new User(1L, "old", "pass", "old@e.com", true, null, null, null);
            mapper.updateEntity(target, null);
            assertThat(target.getUsername()).isEqualTo("old");
        }

        @Test
        @DisplayName("updates only non-null fields")
        void partialUpdate() {
            User target = new User(1L, "old", "oldPass", "old@e.com", true, null, null, null);
            UserUpdateRequest request = new UserUpdateRequest("newUser", null, null, null, null);
            mapper.updateEntity(target, request);
            assertThat(target.getUsername()).isEqualTo("newUser");
            assertThat(target.getPassword()).isEqualTo("oldPass");
            assertThat(target.getEmail()).isEqualTo("old@e.com");
        }

        @Test
        @DisplayName("updates all fields when all set")
        void fullUpdate() {
            User target = new User(1L, "a", "b", "a@b.com", true, null, null, null);
            UserUpdateRequest request = new UserUpdateRequest("x", "y", "x@y.com", false, List.of(2L));
            mapper.updateEntity(target, request);
            assertThat(target.getUsername()).isEqualTo("x");
            assertThat(target.getPassword()).isEqualTo("y");
            assertThat(target.getEmail()).isEqualTo("x@y.com");
            assertThat(target.getEnabled()).isFalse();
        }
    }
}
