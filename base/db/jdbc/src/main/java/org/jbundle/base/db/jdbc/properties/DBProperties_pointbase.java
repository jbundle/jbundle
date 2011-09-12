/*
 * AccessProperties.java
 *
 * Created on March 29, 2001, 2:16 AM

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.jdbc.properties;

import java.util.ListResourceBundle;

import org.jbundle.base.db.SQLParams;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBSQLTypes;


/**
 * AccessProperties - SQL specficics for the MS Access database.
 * @author  don
 * @version 
 */
public class DBProperties_pointbase extends ListResourceBundle
{
  public Object[][] getContents() {
      return contents;
  }
  static final Object[][] contents = {
  // LOCALIZE THIS
      {SQLParams.TABLE_NOT_FOUND_ERROR_TEXT, "Invalid table name"},   // Table not found
      {SQLParams.BIT_TYPE_SUPPORTED, DBConstants.TRUE},
      {DBSQLTypes.DATETIME, "TIMESTAMP"},
      {DBSQLTypes.BOOLEAN, "BOOLEAN"},
      {DBSQLTypes.MEMO, "CLOB(32768)"},
      {"LONGSTRINGSTART", "4000"},
      {"LONGSTRING", "CLOB"},
      {DBSQLTypes.OBJECT, "BLOB(32768)"},
      {SQLParams.SHARE_JDBC_CONNECTION, DBConstants.TRUE},  // HACK for pointbase

      {SQLParams.CREATE_DATABASE_SUPPORTED, DBConstants.TRUE},  // Can create databases.
      {SQLParams.RENAME_TABLE_SUPPORT, DBConstants.TRUE},  // Can rename tables.
      {DBConstants.LOAD_INITIAL_DATA, DBConstants.TRUE},  // Load the initial data

//    {JDBC_DRIVER_PARAM, "com.pointbase.net.netJDBCDriver"},
//    {JDBC_URL_PARAM, "jdbc:pointbase://localhost/<dbname/>,new"},
      {SQLParams.INTERNAL_DB_NAME, "pointbase"},
      {SQLParams.JDBC_DRIVER_PARAM, "com.pointbase.jdbc.jdbcDriver"}, // Pointbase driver
      {SQLParams.DEFAULT_JDBC_URL_PARAM, "jdbc:pointbase:{dbname}"},
      {SQLParams.DEFAULT_USERNAME_PARAM, "public"},
      {SQLParams.DEFAULT_PASSWORD_PARAM, "public"}
      // END OF MATERIAL TO LOCALIZE
  };
 }
