package io.pivotal.demo.multidatasource;

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
		return test1Template.queryForObject("select 1 from dual",Integer.class);
	}
	@GetMapping("/test2")
	public Integer test2() {
		return test1Template.queryForObject("select 1 from dual",Integer.class);
	}
}
@Configuration
@Profile("cloud")
class DataSourceConfiguration extends AbstractCloudConfig {
	@Bean
	public DataSource test1DataSource() {
		return connectionFactory().dataSource("test1-oracle");
	}
	@Bean
	public DataSource test2DataSource() {
		DataSource test2Datasource = connectionFactory().dataSource("test2-oracle");
		return connectionFactory().dataSource("test2-oracle");
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