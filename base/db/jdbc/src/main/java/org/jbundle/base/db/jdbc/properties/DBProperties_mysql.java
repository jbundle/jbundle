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
public class DBProperties_mysql extends ListResourceBundle
{
  public Object[][] getContents() {
      return contents;
  }
  static final Object[][] contents = {
  // LOCALIZE THIS
      {SQLParams.TABLE_NOT_FOUND_ERROR_TEXT, "t exist"},    // Table not found
      {SQLParams.DB_NOT_FOUND_ERROR_TEXT, "Unknown database"},    // Table not found
      {SQLParams.AUTO_SEQUENCE_ENABLED, DBConstants.FALSE},
      {SQLParams.NO_NULL_KEY_SUPPORT, DBConstants.FALSE},    //+DBConstants.TRUE},
      {DBSQLTypes.BOOLEAN, "TINYINT"},
      {DBSQLTypes.MEMO, "TEXT"},
      {DBSQLTypes.OBJECT, "LONGBLOB"},
      {"INNER_JOIN", ","},
      {"INNER_JOIN_ON", "WHERE"},
      {SQLParams.SQL_DATE_FORMAT, "yyyy-MM-dd"},
      {SQLParams.SQL_TIME_FORMAT, "HH:mm:ss"},
      {SQLParams.SQL_DATETIME_FORMAT, "yyyy-MM-dd HH:mm:ss"},
      {SQLParams.SQL_DATE_QUOTE, "\'"},

//    {SQLParams.JDBC_URL_PARAM, "jdbc:mysql://64.30.210.33/<dbname/>"},

      {SQLParams.CREATE_DATABASE_SUPPORTED, DBConstants.TRUE},  // Can create databases.
      {SQLParams.RENAME_TABLE_SUPPORT, DBConstants.TRUE},  // Can rename tables.
      {DBConstants.LOAD_INITIAL_DATA, DBConstants.TRUE},  // Load the initial data
      
      {SQLParams.INTERNAL_DB_NAME, "mysql"},
      {SQLParams.JDBC_DRIVER_PARAM, "com.mysql.jdbc.Driver"},
//      {SQLParams.DEFAULT_DB_SERVER_PARAM, "db001.tourapp.com"},
      {SQLParams.DEFAULT_DB_SERVER_PARAM, "localhost"},
      {SQLParams.DEFAULT_JDBC_URL_PARAM, "jdbc:mysql://{dbserver}/{dbname}?characterEncoding=UTF8"},
      {SQLParams.DEFAULT_USERNAME_PARAM, "tourapp"},
      {SQLParams.DEFAULT_PASSWORD_PARAM, "sa1sa"},
      {SQLParams.DEFAULT_DATASOURCE_PARAM, "jdbc/{dbname}"},
      {SQLParams.DATASOURCE_FACTORY, "org.jbundle.base.db.jdbc.datasource.MySQLDatasourceFactory"}
      // END OF MATERIAL TO LOCALIZE
  };
 }
