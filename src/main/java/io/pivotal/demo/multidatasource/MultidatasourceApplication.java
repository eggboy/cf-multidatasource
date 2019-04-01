package io.pivotal.demo.multidatasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@SpringBootApplication
public class MultidatasourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultidatasourceApplication.class, args);
    }

}

@RestController
class OracleDatasourceTest {

    JdbcTemplate test1Template;
    JdbcTemplate test2Template;

    public OracleDatasourceTest(JdbcTemplate test1Template, JdbcTemplate test2Template) {
        this.test1Template = test1Template;
        this.test2Template = test2Template;
    }

    @GetMapping("/test1")
    public Integer test1() {
        return test1Template.queryForObject("select 1 from dual", Integer.class);
    }

    @GetMapping("/test2")
    public Integer test2() {
        return test1Template.queryForObject("select 1 from dual", Integer.class);
    }
}

@Configuration
@Profile("cloud")
class DataSourceConfiguration extends AbstractCloudConfig {
    @Bean
    public DataSource test1DataSource() {
        HikariConfig jdbcConfig = new HikariConfig();
        jdbcConfig.setDataSource(connectionFactory().dataSource("test1-oracle"));
        jdbcConfig.setMinimumIdle(5);
        jdbcConfig.setMaximumPoolSize(10);

        return new HikariDataSource(jdbcConfig);
    }

    @Bean
    public DataSource test2DataSource() {
        HikariConfig jdbcConfig = new HikariConfig();
        jdbcConfig.setDataSource(connectionFactory().dataSource("test2-oracle"));
        jdbcConfig.setMinimumIdle(5);
        jdbcConfig.setMaximumPoolSize(10);

        return new HikariDataSource(jdbcConfig);
    }

    @Bean
    JdbcTemplate test1Template() {
        return new JdbcTemplate(test1DataSource());
    }

    @Bean
    JdbcTemplate test2Template() {
        return new JdbcTemplate(test2DataSource());
    }
}