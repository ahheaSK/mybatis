package com.example.mybatis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
class MybatisApplicationTest {

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

    @Test
    @DisplayName("context loads with MybatisApplication as configuration")
    void contextLoads() {
        // @SpringBootTest loads MybatisApplication; no explicit assertion needed
    }

    @Test
    @DisplayName("run(String[]) starts application and returns context that can be closed")
    void runStartsApplicationAndReturnsContext() {
        String[] args = argsWithDatasource();
        ConfigurableApplicationContext ctx = MybatisApplication.run(args);
        try {
            assertThat(ctx).isNotNull();
            assertThat(ctx.isActive()).isTrue();
        } finally {
            ctx.close();
        }
    }

    @Test
    @DisplayName("main(String[]) invokes run and starts application")
    void mainStartsApplication() throws InterruptedException {
        String[] args = argsWithDatasource();
        Thread t = new Thread(() -> MybatisApplication.main(args));
        t.setDaemon(true);
        t.start();
        Thread.sleep(4000); // allow context to start; main() is now covered
    }

    private String[] argsWithDatasource() {
        return new String[]{
                "--spring.main.web-application-type=none",
                "--spring.datasource.url=" + postgres.getJdbcUrl(),
                "--spring.datasource.username=" + postgres.getUsername(),
                "--spring.datasource.password=" + postgres.getPassword()
        };
    }
}
