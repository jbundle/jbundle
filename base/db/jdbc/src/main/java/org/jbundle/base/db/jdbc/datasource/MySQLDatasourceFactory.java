/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.jdbc.datasource;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;

import org.jbundle.base.db.SQLParams;
import org.jbundle.base.db.jdbc.JdbcDatabase;
import org.jbundle.base.model.Utility;


/**
 * This factory sets up the datasource for the specific database engines.
 * @author don
 *
 */
public class MySQLDatasourceFactory extends BaseDatasourceFactory {
    
    public MySQLDatasourceFactory()
    {
        super();
    }
    /**
     * Get and populate the data source object for this database.
     * @param database The JDBC database to create a connection to.
     * @return The datasource.
     */
    public DataSource getDataSource(JdbcDatabase database)
    {
        com.mysql.cj.jdbc.MysqlDataSource dataSource = new com.mysql.cj.jdbc.MysqlDataSource();
        this.setDatasourceParams(database, dataSource);
        return dataSource;
    }
    /**
     * Get and populate the pooled data source object for this database.
     * @param database The JDBC database to create a connection to.
     * @return The pooled datasource.
     */
    public ConnectionPoolDataSource getPooledDataSource(JdbcDatabase database)
    {
        com.mysql.cj.jdbc.MysqlConnectionPoolDataSource dataSource = new com.mysql.cj.jdbc.MysqlConnectionPoolDataSource();
        this.setDatasourceParams(database, dataSource);
        return dataSource;
    }
    /**
     * Set the params for this datasource.
     * @param database
     * @param dataSource
     */
    public void setDatasourceParams(JdbcDatabase database, com.mysql.cj.jdbc.MysqlDataSource dataSource)
    {
        String strURL = database.getProperty(SQLParams.JDBC_URL_PARAM);
        if ((strURL == null) || (strURL.length() == 0))
            strURL = database.getProperty(SQLParams.DEFAULT_JDBC_URL_PARAM);    // Default
        String strServer = database.getProperty(SQLParams.DB_SERVER_PARAM);
        if ((strServer == null) || (strServer.length() == 0))
            strServer = database.getProperty(SQLParams.DEFAULT_DB_SERVER_PARAM);    // Default
        if ((strServer == null) || (strServer.length() == 0))
            strServer = "localhost"; //this.getProperty(DBParams.SERVER);    // ??       
        String strDatabaseName = database.getDatabaseName(true);
        if (strURL != null)
        {
            if (strServer != null)
                strURL = Utility.replace(strURL, "{dbserver}", strServer);
            strURL = Utility.replace(strURL, "{dbname}", strDatabaseName);
        }
        String strUsername = database.getProperty(SQLParams.USERNAME_PARAM);
        if ((strUsername == null) || (strUsername.length() == 0))
            strUsername = database.getProperty(SQLParams.DEFAULT_USERNAME_PARAM); // Default
        String strPassword = database.getProperty(SQLParams.PASSWORD_PARAM);
        if ((strPassword == null) || (strPassword.length() == 0))
            strPassword = database.getProperty(SQLParams.DEFAULT_PASSWORD_PARAM); // Default

        dataSource.setDatabaseName(strDatabaseName);
        if (strServer != null)
            dataSource.setServerName(strServer);
        else
            dataSource.setURL(strURL);
        dataSource.setUser (strUsername);
        dataSource.setPassword (strPassword);
    }
}
