/*
 * AccessProperties.java
 *
 * Created on March 29, 2001, 2:16 AM

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.jdbc.properties;

import java.util.ListResourceBundle;

import org.jbundle.base.db.SQLParams;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBSQLTypes;


/**
 * AccessProperties - SQL specficics for the MS Access database.
 * @author  don
 * @version 
 */
public class DBProperties_sybase extends ListResourceBundle
{
  public Object[][] getContents() {
      return contents;
  }
  static final Object[][] contents = {
  // LOCALIZE THIS
      {SQLParams.AUTO_SEQUENCE_ENABLED, DBConstants.FALSE},
      {DBSQLTypes.BOOLEAN, "TINYINT"},
      {DBSQLTypes.MEMO, "LONG VARCHAR"},
      {DBSQLTypes.OBJECT, "LONG BINARY"},
      {DBSQLTypes.DATETIME, "TIMESTAMP"},
      {SQLParams.AUTO_SEQUENCE_EXTRA, " AUTOINCREMENT"},
      {SQLParams.CREATE_DATABASE_SUPPORTED, DBConstants.TRUE},  // Can create databases.
      {SQLParams.RENAME_TABLE_SUPPORT, DBConstants.TRUE},  // Can rename tables.
      {DBConstants.LOAD_INITIAL_DATA, DBConstants.TRUE},  // Load the initial data

      {SQLParams.INTERNAL_DB_NAME, "sybase"},
      {SQLParams.JDBC_DRIVER_PARAM, "sun.jdbc.odbc.JdbcOdbcDriver"},    // JDBC-ODBC
      {SQLParams.DEFAULT_JDBC_URL_PARAM, "JDBC:ODBC:${dbname}"},
      {SQLParams.DEFAULT_USERNAME_PARAM, "dba"},
      {SQLParams.DEFAULT_PASSWORD_PARAM, "sql"},
      // END OF MATERIAL TO LOCALIZE
  };
 }
