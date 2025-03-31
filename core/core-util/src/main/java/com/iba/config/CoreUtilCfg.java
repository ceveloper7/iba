package com.iba.config;

import com.iba.db.IBAConnection;
import com.iba.db.IBA_DB_PostgreSQL;
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

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = {"com.iba.db"})
@PropertySource("classpath:db/database.properties")
public class CoreUtilCfg {
    private static final Logger logger = LoggerFactory.getLogger(CoreUtilCfg.class);

    @Bean
    public String[] dbNames(){
        return new String[]{
                "PostgreSQL"
        };
    }

    @Bean
    public Class<?>[] dbClasses(){
        return new Class[]{
                IBA_DB_PostgreSQL.class
        };
    }

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

    @Value("${uid}")
    private String uid;

    @Value("${pwd}")
    private String pwd;

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
            hc.setUsername(pwd);
            hc.setPassword(pwd);
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

    @Bean
    public IBA_DB_PostgreSQL dbPostgreSQL(){
        IBA_DB_PostgreSQL iba_db_postgreSQL = new IBA_DB_PostgreSQL();
        iba_db_postgreSQL.setDataSource(dataSource());
        return iba_db_postgreSQL;
    }

//    @Bean
//    public PostgreSQLDB postgreSQLDB(){
//        PostgreSQLDB postgreSQLDB = new PostgreSQLDB();
//        postgreSQLDB.setDataSource(dataSource());
//        return  postgreSQLDB;
//    }

    @Bean
    public IBAConnection ibaConnection(){
        return new IBAConnection();
    }

    @Bean
    public org.postgresql.Driver s_driver(){
        return new Driver();
    }
}
