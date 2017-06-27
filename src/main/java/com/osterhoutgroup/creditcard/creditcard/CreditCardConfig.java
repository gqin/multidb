package com.osterhoutgroup.creditcard.creditcard;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySource({ "classpath:creditcard-db.properties" })
@EnableJpaRepositories(
    basePackages = "com.osterhoutgroup.creditcard.creditcard",
    entityManagerFactoryRef = "creditCardEntityManager",
    transactionManagerRef = "creditCardTransactionManager"
)
public class CreditCardConfig {
    @Autowired
    private Environment env;

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean creditCardEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(creditCardDataSource());
        em.setPackagesToScan(
            new String[] { "com.osterhoutgroup.sharedlibraryplugin.models.creditcard" });

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(Boolean.valueOf(env.getProperty("creditcard.jpa.show-sql")));
        em.setJpaVendorAdapter(vendorAdapter);

        return em;
    }

    @Primary
    @Bean
    public DataSource creditCardDataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(
            env.getProperty("creditcard.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("creditcard.datasource.url"));
        dataSource.setUsername(env.getProperty("creditcard.datasource.username"));
        dataSource.setPassword(env.getProperty("creditcard.datasource.password"));

        return dataSource;
    }

    @Primary
    @Bean
    public PlatformTransactionManager creditCardTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(creditCardEntityManager().getObject());
        return transactionManager;
    }
}
