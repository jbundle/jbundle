/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.jdbc.datasource;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;

import org.jbundle.base.db.jdbc.JdbcDatabase;


/**
 * This factory sets up the datasource for the specific database engines.
 * You MUST override one (or both) of these methods.
 * @author don
 *
 */
public abstract class DatasourceFactory {
    
    /**
     * Get and populate the data source object for this database.
     * @param database The JDBC database to create a connection to.
     * @return The datasource.
     */
    public DataSource getDataSource(JdbcDatabase database)
    {
        return null;
    }
    /**
     * Get and populate the pooled data source object for this database.
     * @param database The JDBC database to create a connection to.
     * @return The pooled datasource.
     */
    public ConnectionPoolDataSource getPooledDataSource(JdbcDatabase database)
    {
        return null;
    }
    /**
     * Get and populate the pooled data source object for this database.
     * @param database The JDBC database to create a connection to.
     * @return The pooled datasource.
     */
    public DataSource getFakePooledDataSource(JdbcDatabase database)
    {
        return null;
    }
}
