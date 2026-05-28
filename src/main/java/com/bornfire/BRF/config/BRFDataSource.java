package com.bornfire.BRF.config;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import oracle.jdbc.pool.OracleDataSource;

@Configuration
@EnableTransactionManagement
@ConfigurationProperties("datasrc")
@EnableJpaRepositories(basePackages = "com.bornfire.BRF.entities", entityManagerFactoryRef = "datasrc", transactionManagerRef = "datasrcTransactionManager")
public class BRFDataSource {

	@NotNull
	private String username;
	@NotNull
	private String password;
	@NotNull
	private String url;

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Bean
	public LocalSessionFactoryBean datasrc() throws SQLException {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(srcdataSource());
		sessionFactory.setPackagesToScan("com.bornfire.BRF.entities");
		sessionFactory.setHibernateProperties(hibernateProperties());

		return sessionFactory;
	}

	private final Properties hibernateProperties() {
		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
		return hibernateProperties;
	}

	@Bean
	public DataSource srcdataSource() {
	    com.zaxxer.hikari.HikariDataSource ds = new com.zaxxer.hikari.HikariDataSource();
	    ds.setJdbcUrl(url);
	    ds.setUsername(username);
	    ds.setPassword(password);
	    ds.setDriverClassName("oracle.jdbc.OracleDriver");
	    ds.setMaximumPoolSize(20);
	    ds.setMinimumIdle(5);
	    ds.setIdleTimeout(30000);
	    ds.setConnectionTimeout(60000);
	    ds.setMaxLifetime(1800000);
	    return ds;
	}

	@Bean
	public PlatformTransactionManager datasrcTransactionManager() throws SQLException {

		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(datasrc().getObject());
		return transactionManager;
	}
}
