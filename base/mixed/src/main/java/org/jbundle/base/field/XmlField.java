/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * @(#)HtmlField.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.HashMap;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;


/**
 * A special version of the MemoField for Xml Strings.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class XmlField extends MemoField
{
	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_SCHEMA = "HELP";
    protected String m_strSchema = DEFAULT_SCHEMA;

    /**
     * Constructor.
     */
    public XmlField()
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
    public XmlField(Record record,String strName,int iDataLength,String strDesc,Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Initialize this object.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, iDataLength, strDesc, strDefault);
        if (iDataLength == DBConstants.DEFAULT_FIELD_LENGTH)
            m_iMaxLength = BIG_DEFAULT_LENGTH;
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        BaseField field = new XmlField(null, m_strFieldName, m_iMaxLength, m_strFieldDesc, null);
        field.setRecord(m_record);     // Set table without adding to table field list
        return field;
    }
    /**
     * Set up the default screen control for this field.
     * @param itsLocation Location of this component on screen (ie., GridBagConstraint).
     * @param targetScreen Where to place this component (ie., Parent screen or GridBagLayout).
     * @param converter The converter to set the screenfield to.
     * @param iDisplayFieldDesc Display the label? (optional).
     * @return Return the component or ScreenField that is created for this field.
     */
    public ScreenComponent setupDefaultView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert converter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        ScreenComponent screenField = null;
        screenField = super.setupDefaultView(itsLocation, targetScreen, converter, iDisplayFieldDesc, properties);
        properties = new HashMap<String,Object>();
        properties.put(ScreenModel.FIELD, this);
        properties.put(ScreenModel.COMMAND, ScreenModel.EDIT);
        properties.put(ScreenModel.IMAGE, ScreenModel.EDIT);
        ScreenComponent pSScreenField = createScreenComponent(ScreenModel.CANNED_BOX, targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), targetScreen, converter, iDisplayFieldDesc, properties);
        pSScreenField.setRequestFocusEnabled(false);
        return screenField;
    }
    /**
     * Return the schema name for this xml.
     * @return The schema name.
     */
    public String getSchema()
    {
        return m_strSchema;
    }
    /**
     * Return the schema name for this xml.
     * @return The schema name.
     */
    public void setSchema(String strSchema)
    {
        m_strSchema = strSchema;
    }
} 
