package com.iba.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = {"com.iba.db"})
@PropertySource("classpath:db/database.properties")
public class CoreUtilCfg {
    private static final Logger logger = LoggerFactory.getLogger(CoreUtilCfg.class);

    @Value("${driverClass}")
    private String driverClass;

    @Value("${dbHost}")
    private String dbHost;

    @Value("${dbPort}")
    private String dbPort;

    @Value("${dbName}")
    private String dbName;

    @Value("${dbType}")
    private String dbType;

    @Value("${dbUserId}")
    private String dbUserId;

    @Value("${dbUserPwd}")
    private String dbUserPwd;

    @Bean
    public String connectionStringUrl(){
        StringBuilder sb = new StringBuilder("jdbc:postgresql://")
                .append(dbHost)
                .append(":").append(dbPort)
                .append("/").append(dbName)
                .append("?encoding=TRUE");
        return sb.toString();
    }

    @Bean
    public HikariDataSource dataSource(){
        try {
            var hc = new HikariConfig();
            hc.setJdbcUrl(connectionStringUrl());
            hc.setDriverClassName(driverClass);
            hc.setUsername(dbUserId);
            hc.setPassword(dbUserPwd);
            hc.setIdleTimeout(0);
            hc.setMinimumIdle(15);
            hc.setMaximumPoolSize(150);
            hc.setPoolName("IBA_DS");
            hc.addDataSourceProperty("cachePrepStmts" , "true");
            hc.addDataSourceProperty("prepStmtCacheSize" , "250");
            hc.addDataSourceProperty("prepStmtCacheSqlLimit" , "2048");
            return new HikariDataSource(hc);
        }
        catch (Exception ex){
            logger.error("Hikari Datasource bean cannot be created");
            return null;
        }
    }
}
