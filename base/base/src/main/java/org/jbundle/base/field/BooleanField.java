/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)BooleanField.java    0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.SQLParams;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBSQLTypes;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.thin.base.db.Constants;


/**
 * BooleanField - Set up the YesNo field.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class BooleanField extends NumberField
{
	private static final long serialVersionUID = 1L;

    /**
     * Yes string.
     */
    public static final String YES = "Y";
    /**
     * No string.
     */
    public static final String NO = "N";
    /**
     * The sql string to use for false values.
     */
    public static final String SQLFALSE = "False";
    /**
     * The SQL string to use for true values.
     */
    public static final String SQLTRUE = "True";

    /**
     * Constructor.
     */
    public BooleanField()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public BooleanField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * init.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, iDataLength, strDesc, strDefault);
        m_iMaxLength = 1;
        m_classData = Boolean.class;
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        BaseField field = new BooleanField(null, m_strFieldName, m_iMaxLength, m_strFieldDesc, null);
        field.setRecord(m_record);     // Set table without adding to table field list
        return field;
    }
    /**
     * Convert this index to a string.
     * This method return N for 0 and Y for 1.
     * @param index The index to convert.
     * @return The display string.
     */
    public String convertIndexToString(int index)
    {
        String tempString = null;
        if (index == 0)
            tempString = NO;
        else if (index == 1)
            tempString = YES;
        return tempString;
    }
    /**
     * Convert the field's value to a index (for popup) (usually overidden).
     * @param tempString The string to convert to an index.
     * @return The resulting index.
     */
    public int convertStringToIndex(String tempString)
    {
        String tempChar = tempString;
        if (tempChar.length() == 0)
            tempChar = " ";
        if ((tempChar.charAt(0) == 'Y') || (tempChar.charAt(0) == 'y'))
            return 1;
        else
            return 0;
    }
    /**
     * Retrieve (in string format) from this field.
     * This method is used for SQL calls (ie., WHERE Id=5 AND Name="Don")
     * @return String the ' = "Don"' portion.
     * @param   strSeekSign The sign in the comparison.
     * @param   strCompare  Use the current field values or plug in a '?'
     * @param   bComparison true if this is a comparison, otherwise this is an assignment statement.
     */
    public String getSQLFilter(String strSeekSign, String strCompare, boolean bComparison) 
    {
        if (strCompare == null)
        { // Use the current field value
            Boolean bField = (Boolean)this.getData(); // Get the physical data
            if (bField != null)
            {
                boolean bValue = bField.booleanValue();
                if (bValue == false)
                    strCompare = SQLFALSE;
                else
                    strCompare = SQLTRUE;
            }
        }
        return super.getSQLFilter(strSeekSign, strCompare, bComparison);
    }
    /**
     * Get the SQL type of this field.
     * Typically BOOLEAN or BYTE.
     * @param bIncludeLength Include the field length in this description.
     * @param properties Database properties to determine the SQL type.
     */
    public String getSQLType(boolean bIncludeLength, Map<String, Object> properties) 
    {
        String strType = (String)properties.get(DBSQLTypes.BOOLEAN);
        if (strType == null)
            strType = DBSQLTypes.BYTE;      // The default SQL Type (Byte)
        return strType;
    }
    /**
     * Move the physical binary data to this SQL parameter row.
     * @param statement The SQL prepare statement.
     * @param iType the type of SQL statement.
     * @param iParamColumn The column in the prepared statement to set the data.
     * @exception SQLException From SQL calls.
     */
    public void getSQLFromField(PreparedStatement statement, int iType, int iParamColumn) throws SQLException
    {
        if (this.isNull())
            statement.setNull(iParamColumn, Types.BIT);
        else
        {
            String strBitSupported = DBConstants.TRUE;
            if (this.getRecord() != null)
                if (this.getRecord().getTable() != null)
                    if (this.getRecord().getTable().getDatabase().getProperties() != null)
                strBitSupported = (String)this.getRecord().getTable().getDatabase().getProperties().get(SQLParams.BIT_TYPE_SUPPORTED);
            if (DBConstants.TRUE.equals(strBitSupported))
                statement.setBoolean(iParamColumn, this.getState());
            else
                statement.setByte(iParamColumn, (this.getState() ? (byte)1 : (byte)0));
        }
    }
    /**
     * Move the physical binary data to this SQL parameter row.
     * @param resultset The resultset to get the SQL data from.
     * @param iColumn the column in the resultset that has my data.
     * @exception SQLException From SQL calls.
     */
    public void moveSQLToField(ResultSet resultset, int iColumn) throws SQLException
    {
        boolean bResult = false;
        if (DBConstants.TRUE.equals(this.getRecord().getTable().getDatabase().getProperties().get(SQLParams.BIT_TYPE_SUPPORTED)))
            bResult = resultset.getBoolean(iColumn);
        else
            bResult = resultset.getByte(iColumn) == 0 ? false: true;
        if (resultset.wasNull())
            this.setString(Constants.BLANK, false, DBConstants.READ_MOVE);  // Null value
        else
            this.setState(bResult, false, DBConstants.READ_MOVE);
    }
    /**
     * Get the Value of this field as a double.
     * For a boolean, return 0 for false 1 for true.
     * @return The value of this field.
     */
    public double getValue()
    {           // Get this field's value
        Boolean bField = (Boolean)this.getData(); // Get the physical data
        if (bField == null)
            return 0;
        boolean bValue = bField.booleanValue();
        if (bValue == false)
            return 0;
        else
            return 1;
    }
    /**
     * Set the Value of this field as a double.
     * For a boolean, 0 for false 1 for true.
     * @param value The value of this field.
     * @param bDisplayOption If true, display the new field.
     * @param iMoveMode The move mode.
     * @return An error code (NORMAL_RETURN for success).
     */
    public int setValue(double value, boolean bDisplayOption, int iMoveMode)
    {           // Set this field's value
        Boolean bField = Boolean.FALSE;
        if (value != 0)
            bField = Boolean.TRUE;
        int errorCode = this.setData(bField, bDisplayOption, iMoveMode);
        return errorCode;
    }
    /**
     * Set to the min or max.
     * false is highest for boolean fields.
     * @param iAreaDesc END_SELECT_KEY means set to largest value, others mean smallest.
     */
    public void setToLimit(int iAreaDesc)   // Set this field to the largest or smallest value
    {   // By default compare as ASCII strings
        Boolean bFlag = Boolean.TRUE;   // Lowest value (in SQL)
        if (iAreaDesc == DBConstants.END_SELECT_KEY)
            bFlag = Boolean.FALSE;      // Highest value (in SQL - Alpha order)
        this.doSetData(bFlag, DBConstants.DONT_DISPLAY, DBConstants.SCREEN_MOVE);
    }
    /**
     * Set up the default screen control for this field (A SCheckBox).
     * @param itsLocation Location of this component on screen (ie., GridBagConstraint).
     * @param targetScreen Where to place this component (ie., Parent screen or GridBagLayout).
     * @param converter The converter to set the screenfield to.
     * @param iDisplayFieldDesc Display the label? (optional).
     * @return Return the component or ScreenField that is created for this field.
     * For a Yes/No BaseField, the default is a check box.
     */
    public ScreenComponent setupDefaultView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert converter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        return createScreenComponent(ScreenModel.CHECK_BOX, itsLocation, targetScreen, converter, iDisplayFieldDesc, properties);
    }
    /**
     * Move this physical binary data to this field.
     * @param vpData The physical data to move to this field (must be the Boolean class).
     * @param bDisplayOption If true, display after setting the data.
     * @param moveMode The type of move.
     * @return an error code (0 if success).
     */
    public int doSetData(Object vpData, boolean bDisplayOption, int moveMode)
    {
        if ((vpData != null) && (!(vpData instanceof Boolean)))
            return DBConstants.ERROR_RETURN;
        return super.doSetData(vpData, bDisplayOption, moveMode);
    }
    /**
     * Convert this field's binary data to a string.
     * @param tempBinary The physical data convert to a string (must be the raw data class).
     * @return A display string representing this binary data.
     */
    public String binaryToString(Object tempBinary) 
    {
        String tempString;
        boolean bFlag = false;
        if (tempBinary == null)
            return Constants.BLANK;   // Special case - unknown value
        else
            bFlag = ((Boolean)tempBinary).booleanValue();
        if (bFlag == true)
            tempString = YES;
        else
            tempString = NO;
        return tempString;
    }
    /**
     * Convert this field's binary data to a string.
     * @param tempString The physical data convert to a string (must be the raw data class).
     * @return A display string representing this binary data.
     */
    public Object stringToBinary(String tempString) throws Exception
    {
        Boolean bFlag = Boolean.FALSE;
        if ((tempString == null) || (tempString.length() == 0))
            return null;
        if ((tempString.charAt(0) == 'Y') || (tempString.charAt(0) == 'y') || (tempString.charAt(0) == '1'))
            bFlag = Boolean.TRUE;
        return bFlag;
    }
    /**
     * Read the physical data from a stream file and set this field.
     * @param daIn Input stream to read this field's data from.
     * @param bFixedLength If false (default) be sure to save the length in the stream.
     * @return boolean Success?
     */
    public boolean read(DataInputStream daIn, boolean bFixedLength) // Fixed length = false
    {
        try   {
            boolean bData = daIn.readBoolean();
            Boolean boData = new Boolean(bData);
            int errorCode = this.setData(boData, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);
            return (errorCode == DBConstants.NORMAL_RETURN);    // Success
        } catch (IOException ex)    {
            ex.printStackTrace();
            return false;
        }
    }
    /**
     * Write the physical data in this field to a stream file.
     * @param daOut Output stream to add this field to.
     * @param bFixedLength If false (default) be sure to get the length from the stream.
     * @return boolean Success?
     */
    public boolean write(DataOutputStream daOut, boolean bFixedLength)
    {
        try   {
            Boolean boData = (Boolean)this.getData();
            boolean bData;
            if (boData == null)
                bData = Boolean.FALSE.booleanValue(); // HACK
            else
                bData = boData.booleanValue();
            daOut.writeBoolean(bData);
            return true;
        } catch (IOException ex)    {
            ex.printStackTrace();
            return false;
        }
    }
}
