package com.clairvista.liveexpert.omaha.server.test.util;

import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan("com.clairvista.liveexpert.omaha.server")
@EnableWebMvc
@EnableTransactionManagement
@PropertySource("file:src/test/resources/test.properties")
@SuppressWarnings("unused")
public class TestWebAppConfig {

   // NOTE: My tests were getting the wrong environment variables, so hard-coding these values
   //       was the most robust fix.
   // TODO: Find a way to prevent WebAppConfig from loading.
   private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
   private static final String DATABASE_DRIVER = "org.hsqldb.jdbcDriver";
   private static final String PROPERTY_NAME_DATABASE_URL = "db.url";
   private static final String DATABASE_URL = "jdbc:hsqldb:mem:butterfly";
   private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";
   private static final String DATABASE_USERNAME = "sa";
   private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
   private static final String DATABASE_PASSWORD = "";
      
   private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
   private static final String HIBERNATE_DIALECT = "org.hibernate.dialect.HSQLDialect";
   private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
   private static final String HIBERNATE_SHOW_SQL = "true";
   private static final String PROPERTY_NAME_HIBERNATE_AUTO_ACTION = "hibernate.hbm2ddl.auto";
   private static final String HIBERNATE_AUTO_ACTION = "create";
   private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";
    
   @Resource
   private Environment env;
   
   @Bean
   public DataSource dataSource() {
      DriverManagerDataSource dataSource = new DriverManagerDataSource();

      dataSource.setDriverClassName(DATABASE_DRIVER);
      dataSource.setUrl(DATABASE_URL);
      dataSource.setUsername(DATABASE_USERNAME);
      dataSource.setPassword(DATABASE_PASSWORD);
      
      return dataSource;
   }
   
   @Bean
   public LocalSessionFactoryBean sessionFactory() {
      LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
      sessionFactoryBean.setDataSource(dataSource());
      sessionFactoryBean.setPackagesToScan(env.getRequiredProperty(PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN));
      sessionFactoryBean.setHibernateProperties(hibProperties());
      return sessionFactoryBean;
   }
   
   private Properties hibProperties() {
      Properties properties = new Properties();

      properties.put(PROPERTY_NAME_HIBERNATE_AUTO_ACTION, HIBERNATE_AUTO_ACTION);
      properties.put(PROPERTY_NAME_HIBERNATE_DIALECT, HIBERNATE_DIALECT);
      properties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, HIBERNATE_SHOW_SQL);
      
      return properties;   
   }
   
   @Bean
   public HibernateTransactionManager transactionManager() {
      HibernateTransactionManager transactionManager = new HibernateTransactionManager();
      transactionManager.setSessionFactory(sessionFactory().getObject());
      return transactionManager;
   }

}
