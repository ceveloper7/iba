package com.iba;

import com.iba.config.CoreUtilCfg;
import com.iba.db.IBA_DB_PostgreSQL;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Application {

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(CoreUtilCfg.class);
        var ibaDBPostgreSQL = ctx.getBean(IBA_DB_PostgreSQL.class);
        System.out.println(ibaDBPostgreSQL.getDescription());
    }
}
