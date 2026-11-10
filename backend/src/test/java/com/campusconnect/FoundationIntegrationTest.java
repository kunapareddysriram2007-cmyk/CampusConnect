package com.campusconnect;

import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FoundationIntegrationTest {

    @Test
    void applicationStartsAndFlywayAppliesFoundationMigrationToMySql() {
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(CampusConnectApplication.class)
                .web(WebApplicationType.NONE)
                .run(
                        "--spring.datasource.url=jdbc:tc:mysql:8.4.13:///campusconnect",
                        "--spring.datasource.username=test",
                        "--spring.datasource.password=test")) {
            JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
            Integer appliedMigrationCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM flyway_schema_history WHERE version = '1' AND success = 1",
                    Integer.class);
            assertEquals(1, appliedMigrationCount);
        }
    }
}
