package com.example.CryptocurrencyMarketAnalysisSystem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PostgreSQLContainerTest {

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testSchemaSqlExecuted() {
        try (PostgreSQLContainer<?> ignored = postgresContainer) {
            assertNotNull(jdbcTemplate, "JdbcTemplate should be injected");

            String sql = "CREATE TABLE IF NOT EXISTS SCHEMA_LOG (log_message VARCHAR(255));";
            jdbcTemplate.execute(sql);
            sql = "INSERT INTO SCHEMA_LOG (log_message) VALUES ('schema.sql executed');";
            jdbcTemplate.execute(sql);

            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM SCHEMA_LOG WHERE log_message = 'schema.sql executed'", Integer.class);
            assertNotNull(count, "Count should not be null");
            assertTrue(count > 0, "schema.sql should be executed and log message inserted");
        }
    }
    @Test
    void testCandlesticksTableExists() {
        try (PostgreSQLContainer<?> ignored = postgresContainer) {
            assertNotNull(jdbcTemplate, "JdbcTemplate should be injected");
            String sql = "CREATE TABLE IF NOT EXISTS CANDLESTICKS (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "symbol VARCHAR(255) NOT NULL, " +
                "open_time TIMESTAMP NOT NULL, " +
                "close_time TIMESTAMP NOT NULL, " +
                "open DECIMAL(20, 8) NOT NULL, " +
                "close DECIMAL(20, 8) NOT NULL, " +
                "high DECIMAL(20, 8) NOT NULL, " +
                "low DECIMAL(20, 8) NOT NULL, " +
                "volume DECIMAL(20, 8) NOT NULL);";
            jdbcTemplate.execute(sql);

            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'CANDLESTICKS'", Integer.class);
            assertNotNull(count, "Count should not be null");
            assertTrue(count > 0, "Table CANDLESTICKS should exist");
        }
    }
}
