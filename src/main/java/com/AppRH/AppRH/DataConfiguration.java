package com.AppRH.AppRH;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

/**
 * DataConfiguration — Configuração manual de DataSource e JpaVendorAdapter.
 *
 * As propriedades são lidas do arquivo application.properties via anotação @Value.
 * Isso permite usar variáveis de ambiente em produção sem alterar código.
 *
 * Exemplo de uso com variáveis de ambiente:
 *  - export DB_URL=jdbc:mysql://prod-server:3306/app_rh
 *  - export DB_USER=prod_user
 *  - export DB_PASS=${SECURE_PASSWORD}
 *  - java -jar app.jar
 */
@Configuration
public class DataConfiguration {

    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String driverClassName;

    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/app_rh?useTimezone=true&serverTimezone=UTC}")
    private String url;

    @Value("${spring.datasource.username:root}")
    private String username;

    @Value("${spring.datasource.password:12345678}")
    private String password;

    @Value("${spring.jpa.database-platform:org.hibernate.dialect.MariaDBDialect}")
    private String databasePlatform;

    @Value("${spring.jpa.show-sql:true}")
    private Boolean showSql;

    @Value("${spring.jpa.generate-ddl:true}")
    private Boolean generateDdl;

    /**
     * Define o bean DataSource com base nas propriedades de configuração.
     * @return DataSource configurado
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    /**
     * Define o bean JpaVendorAdapter para Hibernate com configuração MariaDB/MySQL.
     * @return JpaVendorAdapter configurado
     */
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.MYSQL);
        adapter.setShowSql(showSql);
        adapter.setGenerateDdl(generateDdl);
        adapter.setDatabasePlatform(databasePlatform);
        adapter.setPrepareConnection(true);
        return adapter;
    }
}


