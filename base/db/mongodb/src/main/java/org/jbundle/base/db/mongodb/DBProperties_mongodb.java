/*
 * AccessProperties.java
 *
 * Created on March 29, 2001, 2:16 AM

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.mongodb;

import java.util.ListResourceBundle;

import org.bson.BsonType;
import org.jbundle.base.db.SQLParams;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBSQLTypes;


/**
 * AccessProperties - SQL specficics for the MS Access database.
 * @author  don
 * @version 
 */
public class DBProperties_mongodb extends ListResourceBundle
{
  public Object[][] getContents() {
      return contents;
  }
  static final Object[][] contents = {
  // LOCALIZE THIS
      {SQLParams.ALTERNATE_COUNTER_NAME, "_id"},
      {SQLParams.TABLE_NOT_FOUND_ERROR_TEXT, " does not exist"},    // Table not found 42X05"42Y07},
      {SQLParams.AUTO_SEQUENCE_ENABLED, DBConstants.FALSE},
      {SQLParams.COUNTER_OBJECT_CLASS, java.lang.String.class.getName()},  //, java.lang.Integer.class.getName()},  // Default
      {DBSQLTypes.COUNTER, "string"}, // Object
      {SQLParams.NO_NULL_UNIQUE_KEYS, DBConstants.TRUE},	// A null is considered a indexable value and will produce a dup error
      {SQLParams.NO_DUPLICATE_KEY_NAMES, DBConstants.TRUE},
      {SQLParams.MAX_KEY_NAME_LENGTH, "128"},
//      {SQLParams.BIT_TYPE_SUPPORTED, DBConstants.TRUE},
      {DBSQLTypes.DATETIME, "date"},
      {DBSQLTypes.DATE, "date"},
      {DBSQLTypes.TIME, "date"},   // timestamp?
      {DBSQLTypes.BOOLEAN, "bool"},
      {DBSQLTypes.FLOAT, "double"},
      {DBSQLTypes.CURRENCY, "double"},  // decimal?
      {DBSQLTypes.MEMO, "string"},
      {DBSQLTypes.SHORT, "int"},
      {DBSQLTypes.INTEGER, "int"},
      {DBSQLTypes.OBJECT, "binData"},
      {DBSQLTypes.BYTE, "int"},
      {DBSQLTypes.DOUBLE, "double"},
      {DBSQLTypes.SMALLINT, "int"},
      {DBSQLTypes.STRING, "string"},
      {"INNER_JOIN", ","},
      {"INNER_JOIN_ON", "WHERE"},
//      {SQLParams.SQL_DATE_FORMAT, "yyyy-MM-dd"},
//      {SQLParams.SQL_TIME_FORMAT, "HH:mm:ss"},
//      {SQLParams.SQL_DATETIME_FORMAT, "yyyy-MM-dd HH:mm:ss"},
//      {SQLParams.SQL_DATE_QUOTE, "\'"},
//      {SQLParams.CREATE_PRIMARY_INDEX, "alter table {table} add CONSTRAINT {table}_{keyname} PRIMARY KEY({fields})"},
//      {SQLParams.CREATE_INDEX, ""}, // Blank = Not supported
//      {SQLParams.ALT_SECONDARY_INDEX, "INDEX_BLIST"}, // Alt method supported (This will speed things up a little).
      {SQLParams.CREATE_DATABASE_SUPPORTED, DBConstants.TRUE},  // Can create databases.
//?      {SQLParams.RENAME_TABLE_SUPPORT, DBConstants.TRUE},  // Can rename tables.
      {SQLParams.AUTO_COMMIT_PARAM, DBConstants.TRUE},
      {DBConstants.LOAD_INITIAL_DATA, DBConstants.TRUE},  // Load the initial data

      {SQLParams.INTERNAL_DB_NAME, "mongodb"},
      {SQLParams.DEFAULT_JDBC_URL_PARAM, "mongodb://${username}:${password}@${dbserver}"},
      {SQLParams.DEFAULT_USERNAME_PARAM, "tourgeek"},
      {SQLParams.DEFAULT_PASSWORD_PARAM, "sa1sa"}
  };
      // END OF MATERIAL TO LOCALIZE
 }
