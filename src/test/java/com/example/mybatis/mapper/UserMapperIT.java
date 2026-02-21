package com.example.mybatis.mapper;

import com.example.mybatis.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
class UserMapperIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserMapper userMapper;

    @Nested
    @DisplayName("insert and selectById")
    class InsertAndSelect {
        @Test
        @DisplayName("inserts user and selectById returns it")
        void success() {
            User user = new User(null, "mapperuser", "secret", "mapper@test.com", true, null, null, null);
            userMapper.insert(user);
            assertThat(user.getId()).isNotNull();

            User found = userMapper.selectById(user.getId());
            assertThat(found).isNotNull();
            assertThat(found.getUsername()).isEqualTo("mapperuser");
            assertThat(found.getEmail()).isEqualTo("mapper@test.com");
        }
    }

    @Nested
    @DisplayName("selectByUsername")
    class SelectByUsername {
        @Test
        @DisplayName("returns user when username exists")
        void success() {
            User user = new User(null, "uniquename", "pwd", "u@test.com", true, null, null, null);
            userMapper.insert(user);

            User found = userMapper.selectByUsername("uniquename");
            assertThat(found).isNotNull();
            assertThat(found.getUsername()).isEqualTo("uniquename");
        }
    }

    @Nested
    @DisplayName("selectByCondition and countByCondition")
    class SelectByCondition {
        @Test
        @DisplayName("returns matching users and count")
        void success() {
            User u = new User(null, "cond_user1", "p", "c1@test.com", true, null, null, null);
            userMapper.insert(u);
            u = new User(null, "cond_user2", "p", "c2@test.com", true, null, null, null);
            userMapper.insert(u);

            List<User> list = userMapper.selectByCondition("cond_user", null, 0, 10);
            long count = userMapper.countByCondition("cond_user", null);
            assertThat(list).hasSizeGreaterThanOrEqualTo(2);
            assertThat(count).isGreaterThanOrEqualTo(2);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        @DisplayName("updates user")
        void success() {
            User user = new User(null, "upduser", "p", "upd@test.com", true, null, null, null);
            userMapper.insert(user);
            user.setEmail("new@test.com");
            int rows = userMapper.update(user);
            assertThat(rows).isEqualTo(1);

            User found = userMapper.selectById(user.getId());
            assertThat(found.getEmail()).isEqualTo("new@test.com");
        }
    }

    @Nested
    @DisplayName("deleteById")
    class Delete {
        @Test
        @DisplayName("soft-deletes and selectById returns null")
        void success() {
            User user = new User(null, "deluser", "p", "del@test.com", true, null, null, null);
            userMapper.insert(user);
            Long id = user.getId();
            int rows = userMapper.deleteById(id);
            assertThat(rows).isEqualTo(1);

            User found = userMapper.selectById(id);
            assertThat(found).isNull();
        }
    }
}
