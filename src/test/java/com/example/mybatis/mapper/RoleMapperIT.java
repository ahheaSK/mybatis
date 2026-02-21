package com.example.mybatis.mapper;

import com.example.mybatis.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("dev")
class RoleMapperIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("testdb").withUsername("test").withPassword("test");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private RoleMapper roleMapper;

    @Test
    void insertAndSelectById() {
        Role role = new Role(null, "TEST_ROLE", "Test Role", "For tests", null);
        roleMapper.insert(role);
        assertThat(role.getId()).isNotNull();
        Role found = roleMapper.selectById(role.getId());
        assertThat(found).isNotNull();
        assertThat(found.getCode()).isEqualTo("TEST_ROLE");
    }

    @Test
    void selectByConditionAndCount() {
        roleMapper.insert(new Role(null, "FILTER_A", "Filter A", null, null));
        roleMapper.insert(new Role(null, "FILTER_B", "Filter B", null, null));
        List<Role> list = roleMapper.selectByCondition("FILTER", null, 0, 10);
        long count = roleMapper.countByCondition("FILTER", null);
        assertThat(list).hasSizeGreaterThanOrEqualTo(2);
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    @Test
    void update() {
        Role role = new Role(null, "UPD_ROLE", "Before", null, null);
        roleMapper.insert(role);
        role.setName("After");
        assertThat(roleMapper.update(role)).isEqualTo(1);
        assertThat(roleMapper.selectById(role.getId()).getName()).isEqualTo("After");
    }

    @Test
    void deleteById_softDelete() {
        Role role = new Role(null, "DEL_ROLE", "To Delete", null, null);
        roleMapper.insert(role);
        assertThat(roleMapper.deleteById(role.getId(), null)).isEqualTo(1);
        assertThat(roleMapper.selectById(role.getId())).isNull();
    }
}
