package com.iba.db;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class IBADatabases implements ApplicationContextAware {

    private ApplicationContext ctx;
    public static int CONNECTION_TIMEOUT = 10;

   public static String IBA_DB_POSTGRESQL = "PostgreSQL";

    // Supported Databases
    public static String[] DB_NAMES = new String[]{
            IBA_DB_POSTGRESQL
    };

    // Database Classes
    protected static Class<?>[] DB_CLASSES = new Class[]{
            IBA_DB_PostgreSQL.class
    };

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

    public IBAGeneralDatabase getDatabase(String type)throws Exception{
        IBAGeneralDatabase db = null;
        for(int i = 0; i < IBADatabases.DB_NAMES.length; i++){
            if(IBADatabases.DB_NAMES[i].equals(type)){
                db = (IBAGeneralDatabase) ctx.getBean(IBADatabases.DB_CLASSES[i]);
                break;
            }
        }
        return db;
    }

    public IBAGeneralDatabase getDatabaseFromURL(String url){
        if(url == null || url.isEmpty()){
            return null;
        }

        if(url.contains("postgresql")){
            return ctx.getBean(IBA_DB_PostgreSQL.class);
        }
        return null;
    }
}
