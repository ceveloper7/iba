package com.iba;

import com.iba.config.CoreUtilCfg;
import com.iba.db.IBAConnection;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Application {

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(CoreUtilCfg.class);
        var connection = ctx.getBean(IBAConnection.class);
        connection.dbOk();
    }
}
