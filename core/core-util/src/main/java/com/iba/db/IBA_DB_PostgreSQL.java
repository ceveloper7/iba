package com.iba.db;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class IBA_DB_PostgreSQL implements IBAGeneralDatabase{
    private static final Logger logger = LoggerFactory.getLogger(IBA_DB_PostgreSQL.class);

    @Autowired
    private org.postgresql.Driver s_driver;

    private HikariDataSource dataSource;

    /** Data Source	Long Running 	*/
    private DataSource datasourceLongRunning = null;

    /** Data Source	Short Running 	*/
    //private DataSource datasourceShortRunning = null;

    @Value("${driverClass}")
    public static String DRIVER;

    @Value("${dbPort}")
    private static int DEFAULT_PORT;

    private String m_userName = null;
    private String m_connectionUrl = null;
    // Cached Databasename
    private String m_dbName = null;

    @Override
    public String getName() {
        return IBADatabases.DB_POSTGRESQL;
    }

    @Override
    public String getDescription() {
        try{
            if(s_driver == null){
                getDriver();
            }
        }
        catch (Exception ex){}

        if(s_driver != null)
            return s_driver.toString();

        return "There is no Driver";
    }

    @Override
    public Driver getDriver() throws SQLException {
        if(s_driver == null){
            DriverManager.registerDriver(s_driver);
            DriverManager.setLoginTimeout(IBADatabases.CONNECTION_TIMEOUT);
        }
        return s_driver;
    }

    @Override
    public int getStandardPort() {
        return DEFAULT_PORT;
    }

    @Override
    public String getConnectionURL(IBAConnection connection) {
        return connection.getConnectionStringUrl();
    }

    @Override
    public String getConnectionURL(String dbHost, int dbPort, String dbName, String userName) {
        StringBuilder sb = new StringBuilder("jdbc:postgresql://")
                .append(dbHost)
                .append(":").append(dbPort)
                .append("/").append(dbName);
        return sb.toString();
    }

    @Override
    public String getConnectionURL(String connectionURL, String userName) {
        m_userName = userName;
        m_connectionUrl = connectionURL;

        return m_connectionUrl;
    }

    @Override
    public String getCatalog() {
        if(m_dbName != null || !m_dbName.isEmpty()){
            return m_dbName;
        }
        return null;
    }

    @Override
    public String getSchema() {
        return "IBA";
    }

    @Override
    public boolean isSupportedBLOB() {
        return true;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("DB_PostgreSQL[");
        sb.append(m_connectionUrl);
        try
        {
            StringBuilder logBuffer = new StringBuilder();
            HikariPoolMXBean mxBean = dataSource.getHikariPoolMXBean();

            logBuffer.append("# Connections: ").append(mxBean.getTotalConnections());
            logBuffer.append(" , # Busy Connections: ").append(mxBean.getActiveConnections());
            logBuffer.append(" , # Idle Connections: ").append(mxBean.getIdleConnections());
            logBuffer.append(" , # Threads waiting on connection: ").append(mxBean.getThreadsAwaitingConnection());
        }
        catch (Exception e)
        {
            sb.append("=").append(e.getLocalizedMessage());
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public DataSource getDataSource(IBAConnection connection){
        if(datasourceLongRunning != null)
            return datasourceLongRunning;

        datasourceLongRunning = dataSource;

        return datasourceLongRunning;
    }

    @Override
    public Connection getFromConnectionPool(IBAConnection connection,
                                            boolean autoCommit, int transactionIsolation) throws Exception{
        if(datasourceLongRunning == null){
            getDataSource(connection);
        }

        Connection localConnection = datasourceLongRunning.getConnection();
        if(localConnection != null){
            localConnection.setAutoCommit(autoCommit);
            localConnection.setTransactionIsolation(transactionIsolation);
        }

        return localConnection;
    }

    @Override
    public Connection getDriverConnection (IBAConnection connection) throws SQLException{
        getDriver();
        return DriverManager.getConnection(getConnectionURL(connection), connection.getUid(), connection.getPwd());
    }

    @Override
    public Connection getDriverConnection (String dbUrl, String dbUid, String dbPwd) throws SQLException{
        getDriver();
        return DriverManager.getConnection(dbUrl, dbUid, dbPwd);
    }

    @Override
    public String getStatus(){
        if(dataSource == null){
            return null;
        }

        StringBuilder sb = new StringBuilder();
        try{
            HikariPoolMXBean mxBean = dataSource.getHikariPoolMXBean();

            sb.append("# Connections: ").append(mxBean.getTotalConnections());
            sb.append(" , # Busy Connections: ").append(mxBean.getActiveConnections());
            sb.append(" , # Idle Connections: ").append(mxBean.getIdleConnections());
            sb.append(" , # Threads waiting on connection: ").append(mxBean.getThreadsAwaitingConnection());
            sb.append(" , # Min Pool Size: ").append(dataSource.getMinimumIdle());
            sb.append(" , # Max Pool Size: ").append(dataSource.getMaximumPoolSize());
            //sb.append(" , # Open Transactions: ").append(Trx.getOpenTransactions().length);
        }
        catch (Exception e){
            System.err.println(sb.toString() + " - " + e.getMessage());
        }
        return sb.toString();
    }

    @Override
    public void close(){
        try{
            dataSource.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Autowired
    public void setDataSource(HikariDataSource dataSource){
        this.dataSource = dataSource;
    }
}
