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
public class DBProperties_access extends ListResourceBundle
{
  public Object[][] getContents() {
      return contents;
  }
  static final Object[][] contents = {
  // LOCALIZE THIS
      {SQLParams.AUTO_SEQUENCE_ENABLED, DBConstants.FALSE},
      {SQLParams.USE_BLOB_ON_LARGE_STRINGS, DBConstants.TRUE},
      {SQLParams.TABLE_NOT_FOUND_ERROR_CODE, "-1305"},
      {SQLParams.BIT_TYPE_SUPPORTED, DBConstants.TRUE},
      {DBSQLTypes.DATETIME, "TIMESTAMP"},
      {DBSQLTypes.BOOLEAN, "BIT"},
      {DBSQLTypes.MEMO, "LONGCHAR"},
      {DBSQLTypes.CURRENCY, "CURRENCY"},
      {DBSQLTypes.FLOAT, "SINGLE"},
      {SQLParams.AUTO_SEQUENCE_TYPE, "COUNTER"},
      {SQLParams.AUTO_SEQUENCE_PRIMARY_KEY, "CONSTRAINT PrimaryKey PRIMARY KEY"},
      {SQLParams.FLOAT_XFER_SUPPORTED, DBConstants.FALSE}, // real object, not just string

      {SQLParams.CREATE_DATABASE_SUPPORTED, DBConstants.FALSE},  // Can create databases.
      {SQLParams.RENAME_TABLE_SUPPORT, DBConstants.FALSE},  // Can rename tables.
      {DBConstants.LOAD_INITIAL_DATA, DBConstants.FALSE},  // Load the initial data

      {SQLParams.INTERNAL_DB_NAME, "access"},
      {SQLParams.JDBC_DRIVER_PARAM, "sun.jdbc.odbc.JdbcOdbcDriver"},    // JDBC-ODBC
      {SQLParams.DEFAULT_JDBC_URL_PARAM, "JDBC:ODBC:{dbname}"},
      {SQLParams.DEFAULT_USERNAME_PARAM, ""},
      {SQLParams.DEFAULT_PASSWORD_PARAM, ""},
      // END OF MATERIAL TO LOCALIZE
  };
 }
