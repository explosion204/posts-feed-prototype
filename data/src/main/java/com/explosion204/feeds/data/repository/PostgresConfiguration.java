package com.explosion204.feeds.data.repository;

import com.explosion204.feeds.data.mapping.Mappers;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.r2dbc.core.DatabaseClient;

import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PORT;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

@Configuration
@PropertySource("classpath:database.properties")
@Import(Mappers.class)
public class PostgresConfiguration {
  private static final String DRIVER_NAME = "postgres";

  @Bean
  public ConnectionFactory connectionFactory(
          @Value("${postgres.host}") String host,
          @Value("${postgres.port}") int port,
          @Value("${postgres.username}") String username,
          @Value("${postgres.password}") String password,
          @Value("${postgres.database}") String database
  ) {
    return ConnectionFactories.get(ConnectionFactoryOptions.builder()
            .option(DRIVER, DRIVER_NAME)
            .option(HOST, host)
            .option(PORT, port)
            .option(USER, username)
            .option(PASSWORD, password)
            .option(DATABASE, database)
            .build());
  }

  @Bean
  public DatabaseClient databaseClient(ConnectionFactory connectionFactory) {
    return DatabaseClient.builder()
            .connectionFactory(connectionFactory)
            .namedParameters(true)
            .build();
  }
}
