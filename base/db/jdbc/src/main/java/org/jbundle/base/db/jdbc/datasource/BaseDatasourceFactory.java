/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.jdbc.datasource;

import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;

import org.jbundle.base.db.SQLParams;
import org.jbundle.base.db.jdbc.JdbcDatabase;
import org.jbundle.base.model.Utility;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;


/**
 * This factory sets up the datasource for the specific database engines.
 * @author don
 *
 */
public abstract class BaseDatasourceFactory extends DatasourceFactory {
    
    public BaseDatasourceFactory()
    {
        super();
    }
    /**
     * Get and populate the pooled data source object for this database.
     * @param database The JDBC database to create a connection to.
     * @return The pooled datasource.
     */
    public DataSource getFakePooledDataSource(JdbcDatabase database)
    {
    	ComboPooledDataSource dataSource = new ComboPooledDataSource();
    	
    	this.setDatasourceParams(database, dataSource);
    	
		return dataSource;
    }
    /**
     * Set the params for this datasource.
     * @param database
     * @param dataSource
     */
    public void setDatasourceParams(JdbcDatabase database, ComboPooledDataSource dataSource)
    {
        if (dataSource != null)
        {   // Try pooled connection first
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
	        
	        String strDriver = database.getProperty(SQLParams.JDBC_DRIVER_PARAM);
	
	    	try {
				dataSource.setDriverClass(strDriver); //loads the jdbc driver            
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
	//        dataSource.setDatabaseName(strDatabaseName);
	//        if (strServer != null)
	//            dataSource.setServerName(strServer);
	//        else
	            dataSource.setJdbcUrl(strURL);
	        dataSource.setUser (strUsername);
	        dataSource.setPassword (strPassword);
	        
        	// the settings below are optional -- c3p0 can work with defaults
//?        	dataSource.setMinPoolSize(5);                                     
//?        	dataSource.setAcquireIncrement(5);
//?        	dataSource.setMaxPoolSize(20);
            //xMiniConnectionPoolManager poolMgr = new MiniConnectionPoolManager(poolDataSource, MAX_POOLED_CONNECTIONS, POOL_TIMEOUT);

        }
    }
}
