package com.test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DSConfig {
    @Autowired
    private Environment env;
    @Autowired
    private DataSource dataSource;

    @Primary
    @Bean()
    public PlatformTransactionManager transactionManager() {
        EntityManagerFactory factory = entityManagerFactory().getObject();
        return new JpaTransactionManager(factory);
    }

    @Bean("entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory =
                new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("com.test.entity");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        Properties jpaProperties = new Properties();
//        jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
//        jpaProperties.put("hibernate.show-sql", env.getProperty("hibernate.show-sql"));
        factory.setJpaProperties(jpaProperties);
        return factory;
    }

}
