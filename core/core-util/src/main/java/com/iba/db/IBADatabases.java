package com.iba.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IBADatabases {
    public static int CONNECTION_TIMEOUT = 10;

    @Value("${dbType}")
    public static String DB_POSTGRESQL;

    @Autowired
    private static IBA_DB_PostgreSQL iba_db_postgreSQL;

    @Autowired
    @Qualifier("dbNames")
    public static String[] DB_NAMES;

    @Autowired
    @Qualifier("dbClasses")
    public static Class<?>[] DB_CLASSES;

    public static IBAGeneralDatabase getDatabase(String type)throws Exception{
        IBAGeneralDatabase db = null;
        for(int i = 0; i < IBADatabases.DB_NAMES.length; i++){
            if(IBADatabases.DB_NAMES[i].equals(type)){
                db = (IBAGeneralDatabase) IBADatabases.DB_CLASSES[i].newInstance();
                break;
            }
        }
        return db;
    }

    public static IBAGeneralDatabase getDatabaseFromURL(String url){
        if(url == null || url.isEmpty())
            return null;
        if(url.contains("postgresql")){
            return iba_db_postgreSQL;
        }
        return null;
    }

}
