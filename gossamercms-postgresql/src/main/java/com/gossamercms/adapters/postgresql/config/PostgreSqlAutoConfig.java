package com.gossamercms.adapters.postgresql.config;

import com.gossamercms.adapters.postgresql.PostgresAdapter;
import com.gossamercms.mvc.data.DataSourceManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@AutoConfiguration
public class PostgreSqlAutoConfig {

    @Bean
    public PostgresAdapter postgresAdapter(
            NamedParameterJdbcTemplate namedJdbcTemplate,
            JdbcTemplate jdbcTemplate
    ) {
        return new PostgresAdapter(
                namedJdbcTemplate,
                jdbcTemplate
        );
    }

    @Bean
    public Object registerPostgresAdapter(
            DataSourceManager manager,
            PostgresAdapter adapter
    ) {
        manager.register(adapter.key(), adapter);
        return new Object();
    }
}