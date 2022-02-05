package tokyomap.oauth;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * cf. https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa
 */
@Configuration
@ComponentScan
@EnableJpaRepositories
@EnableTransactionManagement
@PropertySource("/WEB-INF/jdbc.properties")
public class PersistenceJpaConfig {

  @Value("${db.driverClassName}")  private String driverClassName;
  @Value("${db.url}") private String url;
  @Value("${db.username}") private String username;
  @Value("${db.password}") private String password;

  @Value("${hibernate.dialect}") private String dialect;
  @Value("${hibernate.hbm2ddl.auto}") private String hbm2ddlAuto;
  @Value("${hibernate.show_sql}") private String showSql;
  @Value("${hibernate.format_sql}") private String formatSql;

  @Bean
  public DataSource dataSource(){
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(this.driverClassName);
    dataSource.setUrl(this.url);
    dataSource.setUsername(this.username);
    dataSource.setPassword(this.password);
    return dataSource;
  }

  @Bean
  public Properties additionalProperties() {
    Properties properties = new Properties();
    properties.setProperty("hibernate.dialect", this.dialect);
    properties.setProperty("hibernate.hbm2ddl.auto", this.hbm2ddlAuto);
    properties.setProperty("hibernate.show_sql", this.showSql);
    properties.setProperty("hibernate.format_sql", this.formatSql);
    return properties;
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
    entityManager.setDataSource(dataSource());
    entityManager.setPackagesToScan("tokyomap.oauth.domain");
    JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    entityManager.setJpaVendorAdapter(vendorAdapter);
    entityManager.setJpaProperties(additionalProperties());
    return entityManager;
  }

  @Bean
  public PlatformTransactionManager transactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
    return transactionManager;
  }
}
