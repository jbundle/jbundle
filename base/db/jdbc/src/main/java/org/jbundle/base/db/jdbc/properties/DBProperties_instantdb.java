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
public class DBProperties_instantdb extends ListResourceBundle
{
  public Object[][] getContents() {
      return contents;
  }
  static final Object[][] contents = {
  // LOCALIZE THIS},
      {DBSQLTypes.DATETIME, "DATE"},
      {DBSQLTypes.TIME, "DATE"},
      {DBSQLTypes.MEMO, "LONGCHAR"},
      {DBSQLTypes.OBJECT, "LONGVARBINARY"},
      {DBSQLTypes.CURRENCY, "CURRENCY"},
      {SQLParams.AUTO_SEQUENCE_EXTRA, " AUTO INCREMENT"},
      {SQLParams.COUNTER_EXTRA, " UNIQUE PRIMARY KEY"},
      {SQLParams.AUTO_SEQUENCE_PRIMARY_KEY, "PRIMARY KEY"},

      {SQLParams.CREATE_DATABASE_SUPPORTED, DBConstants.TRUE},  // Can create databases.
      {SQLParams.RENAME_TABLE_SUPPORT, DBConstants.TRUE},  // Can rename tables.
      {DBConstants.LOAD_INITIAL_DATA, DBConstants.TRUE},  // Load the initial data

      {SQLParams.INTERNAL_DB_NAME, "instantdb"},
      {SQLParams.JDBC_DRIVER_PARAM, "jdbc.idbDriver"},
      {SQLParams.DEFAULT_JDBC_URL_PARAM, "jdbc:idb:E:\\data\\database\\idb\\{dbname}\\{dbname}.prp"},
      {SQLParams.DEFAULT_USERNAME_PARAM, ""},
      {SQLParams.DEFAULT_PASSWORD_PARAM, ""},
      // END OF MATERIAL TO LOCALIZE
  };
 }
