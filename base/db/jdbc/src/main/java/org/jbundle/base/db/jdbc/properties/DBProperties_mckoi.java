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
public class DBProperties_mckoi extends ListResourceBundle
{
  public Object[][] getContents() {
      return contents;
  }
  static final Object[][] contents = {
  // LOCALIZE THIS
      {SQLParams.TABLE_NOT_FOUND_ERROR_TEXT, "t exist"},    // Table not found
      {SQLParams.AUTO_SEQUENCE_ENABLED, DBConstants.FALSE},
//      {SQLParams.NO_NULL_KEY_SUPPORT, DBConstants.TRUE},

      {SQLParams.BIT_TYPE_SUPPORTED, DBConstants.TRUE},
//      {DBSQLTypes.BOOLEAN, "TINYINT"},
//      {DBSQLTypes.MEMO, "TEXT"},
      {DBSQLTypes.DATETIME, "TIMESTAMP"},
      {DBSQLTypes.BOOLEAN, "BIT"},
      {DBSQLTypes.MEMO, "TEXT"},
      {DBSQLTypes.OBJECT, "LONGVARBINARY"},
      {"INNER_JOIN", ","},
      {"INNER_JOIN_ON", "WHERE"},
//      {SQLParams.SQL_DATE_FORMAT, "yyyy-MM-dd"},
//      {SQLParams.SQL_TIME_FORMAT, "HH:mm:ss"},
//      {SQLParams.SQL_DATETIME_FORMAT, "yyyy-MM-dd HH:mm:ss"},
//      {SQLParams.SQL_DATE_QUOTE, "\'"},
      {SQLParams.CREATE_PRIMARY_INDEX, "alter table {table} add CONSTRAINT {table}_{keyname} PRIMARY KEY({fields})"},
      {SQLParams.CREATE_INDEX, ""}, // Blank = Not supported
      {SQLParams.ALT_SECONDARY_INDEX, "INDEX_BLIST"}, // Alt method supported (This will speed things up a little).

      {SQLParams.CREATE_DATABASE_SUPPORTED, DBConstants.TRUE},  // Can create databases.
      {SQLParams.RENAME_TABLE_SUPPORT, DBConstants.TRUE},  // Can rename tables.
      {DBConstants.LOAD_INITIAL_DATA, DBConstants.TRUE},  // Load the initial data

      {SQLParams.INTERNAL_DB_NAME, "mckoi"},
      {SQLParams.JDBC_DRIVER_PARAM, "com.mckoi.JDBCDriver"},
      {SQLParams.DEFAULT_JDBC_URL_PARAM, "jdbc:mckoi://{dbserver}/{dbname}"},
      {SQLParams.DEFAULT_USERNAME_PARAM, "admin_user"},
      {SQLParams.DEFAULT_PASSWORD_PARAM, "gi11igan"}
      // END OF MATERIAL TO LOCALIZE
  };
 }
