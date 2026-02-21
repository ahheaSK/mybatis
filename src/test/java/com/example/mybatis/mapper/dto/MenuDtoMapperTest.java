package com.example.mybatis.mapper.dto;

import com.example.mybatis.dto.request.MenuCreateRequest;
import com.example.mybatis.dto.request.MenuUpdateRequest;
import com.example.mybatis.dto.response.MenuResponse;
import com.example.mybatis.entity.Menu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for MapStruct-generated {@link MenuDtoMapperImpl}.
 */
class MenuDtoMapperTest {

    private MenuDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MenuDtoMapperImpl();
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
            MenuCreateRequest request = new MenuCreateRequest("Dashboard", "/dashboard", null, false, false, "Dashboard", null, false, null, "", null, 1, null, "#000000");
            Menu entity = mapper.toEntity(request);
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isNull();
            assertThat(entity.getName()).isEqualTo("Dashboard");
            assertThat(entity.getPath()).isEqualTo("/dashboard");
            assertThat(entity.getTitle()).isEqualTo("Dashboard");
            assertThat(entity.getSortOrder()).isEqualTo(1);
            assertThat(entity.getTextColor()).isEqualTo("#000000");
            assertThat(entity.getParentId()).isNull();
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
            Menu entity = new Menu(1L, "Admin", "/admin", null, true, false, "Admin", "icon", false, null, "", null, 0, null, "#000", null, null, null, null);
            MenuResponse dto = mapper.toDTO(entity);
            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getName()).isEqualTo("Admin");
            assertThat(dto.getPath()).isEqualTo("/admin");
            assertThat(dto.getAlwaysShow()).isTrue();
            assertThat(dto.getTitle()).isEqualTo("Admin");
            assertThat(dto.getIcon()).isEqualTo("icon");
            assertThat(dto.getSortOrder()).isEqualTo(0);
            assertThat(dto.getTextColor()).isEqualTo("#000");
        }
    }

    @Nested
    @DisplayName("updateEntity")
    class UpdateEntity {
        @Test
        @DisplayName("does nothing when request is null")
        void nullRequest() {
            Menu target = new Menu(1L, "OLD", "/old", null, false, false, "Old", null, false, null, "", null, 0, null, "#000", null, null, null, null);
            mapper.updateEntity(target, null);
            assertThat(target.getName()).isEqualTo("OLD");
            assertThat(target.getPath()).isEqualTo("/old");
        }

        @Test
        @DisplayName("updates only non-null fields")
        void partialUpdate() {
            Menu target = new Menu(1L, "OLD", "/old", null, false, false, "Old", null, false, null, "", null, 0, null, "#000", null, null, null, null);
            MenuUpdateRequest request = new MenuUpdateRequest("NEW_NAME", null, null, null, null, null, null, null, null, "", null, null, null, null);
            mapper.updateEntity(target, request);
            assertThat(target.getName()).isEqualTo("NEW_NAME");
            assertThat(target.getPath()).isEqualTo("/old");
            assertThat(target.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("updates all fields when all set")
        void fullUpdate() {
            Menu target = new Menu(1L, "A", "/a", null, false, false, "A", null, false, null, "", null, 0, null, "#0", null, null, null, null);
            MenuUpdateRequest request = new MenuUpdateRequest("X", "/x", "/redirect", true, true, "X", "icon", true, "key", "link", "comp", 10, 2L, "#fff");
            mapper.updateEntity(target, request);
            assertThat(target.getName()).isEqualTo("X");
            assertThat(target.getPath()).isEqualTo("/x");
            assertThat(target.getRedirect()).isEqualTo("/redirect");
            assertThat(target.getAlwaysShow()).isTrue();
            assertThat(target.getHidden()).isTrue();
            assertThat(target.getTitle()).isEqualTo("X");
            assertThat(target.getIcon()).isEqualTo("icon");
            assertThat(target.getNoCache()).isTrue();
            assertThat(target.getTitleKey()).isEqualTo("key");
            assertThat(target.getLink()).isEqualTo("link");
            assertThat(target.getComponent()).isEqualTo("comp");
            assertThat(target.getSortOrder()).isEqualTo(10);
            assertThat(target.getParentId()).isEqualTo(2L);
            assertThat(target.getTextColor()).isEqualTo("#fff");
            assertThat(target.getId()).isEqualTo(1L);
        }
    }
}
