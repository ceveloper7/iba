package com.iba.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

@Component
public class IBAConnection{
    private static final Logger logger = LoggerFactory.getLogger(IBAConnection.class);

    @Autowired
    private IBAConnection ibaConnection;

    @Autowired
    private String connectionStringUrl;

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

    private DataSource m_ds = null;
    private IBAGeneralDatabase m_db = null;
    private boolean m_okDB = false;

    public IBAConnection(){}

    public String getConnectionStringUrl(){
        return connectionStringUrl;
    }

    public Connection getConnection(){
        Connection conn = null;
        String ur = getConnectionStringUrl();

        try{
            if(ur == null || ur.isEmpty()){
                throw new Exception("Unknown database type");
            }
            Class.forName(driverClass);
            conn = DriverManager.getConnection(ur, ur, pwd);
        }
        catch (Exception ex){}

        return conn;
    }

    public String getUid() {
        return uid;
    }

    public String getPwd() {
        return pwd;
    }

    public String getDbType() {
        return dbType;
    }

    public String getDbHost() {
        return dbHost;
    }

    public String getDbPort() {
        return dbPort;
    }

    public String getDbName() {
        return dbName;
    }

    public IBAGeneralDatabase getDatabase(){
        if(m_db != null || !m_db.getName().equals(getDbType())){
            m_db = null;
        }

        if(m_db == null){
            try{
                m_db = IBADatabases.getDatabase(getDbType());
                if(m_db == null){
                    m_db.getDataSource(this);
                }
            }
            catch (NoClassDefFoundError ee){
                System.err.println("Environment Error - Check app.properties - " + ee);
                System.exit(1);
            }
            catch (Exception ee){
                logger.error(ee.toString());
            }
        }
        return m_db;
    }

    public boolean setDataSource(){
        if(m_ds == null){
            IBAGeneralDatabase getDB = getDatabase();
            if(getDB != null)
                m_ds = getDB.getDataSource(this);
        }
        return m_ds != null;
    }

    public boolean setDataSource(DataSource ds){
        if(ds == null && m_db != null)
            getDatabase().close();
        m_ds = ds;

        return m_ds != null;
    }

    public DataSource getDatasource(){
        return m_ds;
    }

    public boolean isDatasource(){
        return m_ds != null;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof IBAConnection){
            IBAConnection cc = (IBAConnection) o;
            if(cc.getDbHost().equals(dbHost) &&
                    cc.getDbPort().equals(dbPort) &&
                    cc.getDbName().equals(dbName) &&
                    cc.getDbType().equals(dbType) &&
                    cc.getUid().equals(uid) &&
                    cc.getPwd().equals(pwd)
            ){
                return true;
            }
        }
        return false;
    }

    public String getConnectionURL(){
        getDatabase();
        if(m_db != null){
            return m_db.getConnectionURL(this);
        }
        else{
            return "";
        }
    }

    public Connection getConnection(boolean autoCommit, int transactionIsolation){
        Connection conn = null;
        m_okDB = false;

        getDatabase();
        if(m_db == null){
            return null;
        }

        try {
            Exception ee = null;
            try {
                conn = m_db.getFromConnectionPool(this, autoCommit, transactionIsolation);
            }
            catch (Exception exception){
                logger.error(exception.getMessage());
                ee = exception;
            }
            if(conn != null) {
                if (conn.getTransactionIsolation() != transactionIsolation)
                    conn.setTransactionIsolation(transactionIsolation);

                if (conn.getAutoCommit() != autoCommit)
                    conn.setAutoCommit(autoCommit);
                m_okDB = true;
            }
        }
        catch (Exception ex){
            System.err.println(getConnectionURL() + " - " + ex.getLocalizedMessage());
        }
        return conn;
    }

    public String getStatus(){
        StringBuilder sb = new StringBuilder();
        sb.append("{").append(dbHost)
                .append("-").append(dbName)
                .append("-").append(uid)
                .append("}");
        if(m_db != null){
            sb.append(m_db.getStatus());
        }
        return sb.toString();
    }

    public static String getTransactionIsolationInfo(int transactionIsolation)
    {
        if (transactionIsolation == Connection.TRANSACTION_NONE)
            return "NONE";
        if (transactionIsolation == Connection.TRANSACTION_READ_COMMITTED)
            return "READ_COMMITTED";
        if (transactionIsolation == Connection.TRANSACTION_READ_UNCOMMITTED)
            return "READ_UNCOMMITTED";
        if (transactionIsolation == Connection.TRANSACTION_REPEATABLE_READ)
            return "REPEATABLE_READ";
        if (transactionIsolation == Connection.TRANSACTION_READ_COMMITTED)
            return "SERIALIZABLE";
        return "<?" + transactionIsolation + "?>";
    }	//	getTransactionIsolationInfo

    public void dbOk(){
        System.out.println(getDbHost());
        IBAConnection s_cc = ibaConnection;
        s_cc.setDataSource();

        try(Connection conn = s_cc.getConnection();
            Statement statement = conn.createStatement();){
            String query = "SELECT version, releaseno FROM system";
            ResultSet rs = statement.executeQuery(query);
            if(rs.next()){
                System.out.println(" Verison No: " + rs.getString(1) + " Realease No: " + rs.getString(2));
            }
            rs.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
