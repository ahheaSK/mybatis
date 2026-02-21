package com.example.mybatis.mapper;

import com.example.mybatis.entity.Permission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
class PermissionMapperIT {

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
    private PermissionMapper permissionMapper;

    @Test
    void insertAndSelectById() {
        Permission p = new Permission(null, "TEST_PERM", "Test Permission", "For tests", null);
        permissionMapper.insert(p);
        assertThat(p.getId()).isNotNull();
        Permission found = permissionMapper.selectById(p.getId());
        assertThat(found).isNotNull();
        assertThat(found.getCode()).isEqualTo("TEST_PERM");
    }

    @Test
    void selectByConditionAndCount() {
        permissionMapper.insert(new Permission(null, "FIND_A", "Find A", null, null));
        permissionMapper.insert(new Permission(null, "FIND_B", "Find B", null, null));
        List<Permission> list = permissionMapper.selectByCondition("FIND", null, 0, 10);
        long count = permissionMapper.countByCondition("FIND", null);
        assertThat(list).hasSizeGreaterThanOrEqualTo(2);
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    @Test
    void update() {
        Permission p = new Permission(null, "UPD_PERM", "Before", null, null);
        permissionMapper.insert(p);
        p.setName("After");
        assertThat(permissionMapper.update(p)).isEqualTo(1);
        assertThat(permissionMapper.selectById(p.getId()).getName()).isEqualTo("After");
    }

    @Test
    void deleteById_hardDelete() {
        Permission p = new Permission(null, "DEL_PERM", "To Delete", null, null);
        permissionMapper.insert(p);
        assertThat(permissionMapper.deleteById(p.getId())).isEqualTo(1);
        assertThat(permissionMapper.selectById(p.getId())).isNull();
    }
}
