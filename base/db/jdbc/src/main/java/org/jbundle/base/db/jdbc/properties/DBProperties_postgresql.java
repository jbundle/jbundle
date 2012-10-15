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
public class DBProperties_postgresql extends ListResourceBundle
{
  public Object[][] getContents() {
      return contents;
  }
  static final Object[][] contents = {
  // LOCALIZE THIS
      {SQLParams.TABLE_NOT_FOUND_ERROR_TEXT, "Table does not exist"}, // Table not found.
      {SQLParams.AUTO_SEQUENCE_ENABLED, DBConstants.FALSE},
      {SQLParams.BIT_TYPE_SUPPORTED, DBConstants.TRUE},
      {DBSQLTypes.DATETIME, "TIMESTAMP"}, // Not tested
      {DBSQLTypes.BOOLEAN, "BOOL"},
      {DBSQLTypes.MEMO, "TEXT"},
      {DBSQLTypes.DOUBLE, "DOUBLE PRECISION"},
      {DBSQLTypes.OBJECT, "TEXT"},  // TEMP

      {SQLParams.CREATE_DATABASE_SUPPORTED, DBConstants.TRUE},  // Can create databases.
      {SQLParams.RENAME_TABLE_SUPPORT, DBConstants.TRUE},  // Can rename tables.
      {DBConstants.LOAD_INITIAL_DATA, DBConstants.TRUE},  // Load the initial data

      {SQLParams.INTERNAL_DB_NAME, "postgresql"},
      {SQLParams.JDBC_DRIVER_PARAM, "postgresql.Driver"}, // JDBC-ODBC
      {SQLParams.DEFAULT_JDBC_URL_PARAM, "jdbc:postgresql://${dbserver}/${dbname}"},
      {SQLParams.DEFAULT_USERNAME_PARAM, "postgres"},
      {SQLParams.DEFAULT_PASSWORD_PARAM, "gi11igan"},
      // END OF MATERIAL TO LOCALIZE
  };
 }
