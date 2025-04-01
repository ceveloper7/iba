package com.iba.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

public interface IBAGeneralDatabase {
    static final int LOCK_TIME_OUT = 60;


    /*  Get Database Name
     *  @return database short name
     */
    public String getName();

    /**
     *  Get Database Description
     *  @return database long name and version
     */
    public String getDescription();

    /**
     *  Get and register Database Driver
     *  @return Driver
     *  @throws SQLException
     */
    public Driver getDriver() throws SQLException;

    /**
     *  Get Standard JDBC Port
     *  @return standard port
     */
    public int getStandardPort();

    /**
     *  Get Database Connection String
     *  @param connection Connection Descriptor
     *  @return connection String
     */
    public String getConnectionURL(IBAConnection connection);


    /* Get Connection URL
     *	@param dbHost db Host
     *	@param dbPort db Port
     *	@param dbName db Name
     *	@param userName user name
     *	@return url
     */
    public String getConnectionURL (String dbHost, int dbPort, String dbName,
                                    String userName);

    /**
     *  Get Database Connection URL
     *  @param connectionURL Connection URL
     *  @param userName user name
     *  @return connection URL
     */
    public String getConnectionURL (String connectionURL, String userName);

    /**
     * 	Get JDBC Catalog
     *	@return catalog
     */
    public String getCatalog();

    /**
     * 	Get JDBC Schema
     *	@return schema
     */
    public String getSchema();

    /**
     *  Supports BLOB
     *  @return true if BLOB is supported
     */
    public boolean isSupportedBLOB();

    /**
     *  String Representation
     *  @return info
     */
    public String toString();

    /**
     * 	Create DataSource
     *	@param connection connection
     *	@return data dource
     */
    public DataSource getDataSource(IBAConnection connection);

    /**
     * 	Get connection from Connection Pool
     *	@param connection info
     *  @param autoCommit true if autocommit connection
     *  @param transactionIsolation Connection transaction level
     *	@return connection or null
     *  @throws Exception
     */
    public Connection getFromConnectionPool(IBAConnection connection,
                                            boolean autoCommit, int transactionIsolation) throws Exception;

    /**
     * 	Get Connection from Driver
     *	@param connection info
     *	@return connection or null
     *  @throws SQLException
     */
    public Connection getDriverConnection (IBAConnection connection) throws SQLException;

    /**
     * 	Get Driver Connection
     *	@param dbUrl URL
     *	@param dbUid user
     *	@param dbPwd password
     *	@return connection
     *	@throws SQLException
     */
    public Connection getDriverConnection (String dbUrl, String dbUid, String dbPwd)
            throws SQLException;


    /**
     * 	Get Status
     * 	@return status info or null if no local datasource available
     */
    public String getStatus();

    /**
     * 	Close
     */
    public void close();
}
