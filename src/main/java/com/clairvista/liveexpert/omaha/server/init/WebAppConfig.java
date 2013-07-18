package com.clairvista.liveexpert.omaha.server.init;

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
@PropertySource("classpath:application.properties")
public class WebAppConfig {

   // These can be overriden by setting JAVA_OPTS for them.
   private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
   private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
   private static final String PROPERTY_NAME_DATABASE_URL = "db.url";
   private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";

   private static final String PROPERTY_NAME_HIBERNATE_AUTO_ACTION = "hibernate.hbm2ddl.auto";
   private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
   private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
   private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";
    
   @Resource
   private Environment env;
   
   @Bean
   public DataSource dataSource() {
      DriverManagerDataSource dataSource = new DriverManagerDataSource();

      dataSource.setDriverClassName(env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
      dataSource.setUrl(env.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
      dataSource.setUsername(env.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
      dataSource.setPassword(env.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));

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

      properties.put(PROPERTY_NAME_HIBERNATE_AUTO_ACTION, "validate");
      properties.put(PROPERTY_NAME_HIBERNATE_DIALECT, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
      properties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));
      
      return properties;   
   }
   
   @Bean
   public HibernateTransactionManager transactionManager() {
      HibernateTransactionManager transactionManager = new HibernateTransactionManager();
      transactionManager.setSessionFactory(sessionFactory().getObject());
      return transactionManager;
   }

}
