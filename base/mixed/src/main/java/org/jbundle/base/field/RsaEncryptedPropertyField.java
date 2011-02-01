package org.jbundle.base.field;

/**
 * @(#)PhoneField.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.Record;
import org.jbundle.base.field.convert.FieldConverter;
import org.jbundle.base.field.convert.FieldLengthConverter;
import org.jbundle.base.field.convert.PropertiesConverter;
import org.jbundle.base.field.convert.encode.RsaEncryptedConverter;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.SEditText;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.db.Converter;


/**
 * Extends the property field and saved three values:
 * The public key encrypted value.
 * The field length
 * The last four digits of the field
 * - Displays asterisks plus the last four digits of the field
 */
public class RsaEncryptedPropertyField extends PropertiesField
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * If true, decrypt field to original (not masked) field
	 */
	private boolean m_bDecryptField = false;

    /**
     * Constructor.
     */
    public RsaEncryptedPropertyField()
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
    public RsaEncryptedPropertyField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Initialize the member fields.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, DBConstants.DEFAULT_FIELD_LENGTH, strDesc, strDefault);
        if (iDataLength == DBConstants.DEFAULT_FIELD_LENGTH)
            m_iFakeFieldLength = 24;
        else
            m_iFakeFieldLength = iDataLength;
    }
    private int m_iFakeFieldLength = -1;
    /**
     * Set up the default screen control for this field.
     * @param itsLocation Location of this component on screen (ie., GridBagConstraint).
     * @param targetScreen Where to place this component (ie., Parent screen or GridBagLayout).
     * @param converter The converter to set the screenfield to.
     * @param iDisplayFieldDesc Display the label? (optional).
     * @return Return the component or ScreenField that is created for this field.
     */
    public ScreenField setupDefaultView(ScreenLocation itsLocation, BasePanel targetScreen, Converter converter, int iDisplayFieldDesc)
    {

        converter = new PropertiesConverter(converter, "value");
        converter = new RsaEncryptedConverter(converter);
        converter = new FieldLengthConverter(converter, m_iFakeFieldLength);

        converter = new RsaPropertyConverter(converter);
        ScreenField screenField = new SEditText(itsLocation, targetScreen, converter, iDisplayFieldDesc);
        return screenField;
    }
    /**
     * Special converter to save all the info in the properties field.
     * @author don
     *
     */
    class RsaPropertyConverter extends FieldConverter
    {
        /**
         * Constructor.
         */
        public RsaPropertyConverter()
        {
            super();
        }
        /**
         * Constructor.
         * @param converter The next converter in the converter chain.
         */
        public RsaPropertyConverter(Converter converter)
        {
            this();
            this.init(converter);
        }
        /**
         * Initialize this converter.
         * @param converter The next converter in the converter chain.
         */
        public void init(Converter converter)
        {
            super.init(converter);
        }
        /**
         * Convert and move string to this field.
         * Override this method to convert the String to the actual Physical Data Type.
         * @param strString the state to set the data to.
         * @param bDisplayOption Display the data on the screen if true.
         * @param iMoveMode INIT, SCREEN, or READ move mode.
         * @return The error code (or NORMAL_RETURN).
         */
        public int setString(String strString, boolean bDisplayOption, int iMoveMode)    // init this field override for other value
        {
            int iErrorCode = super.setString(strString, bDisplayOption, iMoveMode);
            if (this.getField() instanceof PropertiesField)
            {   // Always
                PropertiesField field = (PropertiesField)this.getField();
                int iStringLength = 0;
                if (strString != null)
                    iStringLength = strString.length();
                field.setProperty("length", Integer.toString(iStringLength));
                if (iStringLength > 4)
                    field.setProperty("last4", strString.substring(strString.length() - 4));
                else
                    field.setProperty("last4", null);
            }
            return iErrorCode;
        }
        /**
         * Retrieve (in string format) from this field.
         * @return The data in string format.
         */
        public String getString()
        {
            String string = null;   // By default, DO NOT Decrypt
            if (m_bDecryptField)
                return super.getString();
            if (this.getField() instanceof PropertiesField)
            {   // Always
                PropertiesField field = (PropertiesField)this.getField();
                int iStringLength = 0;
                if (field.getProperty("length") != null)
                    iStringLength = Integer.parseInt(field.getProperty("length"));
                StringBuffer sb = new StringBuffer();
                if (field.getProperty("last4") != null)
                    sb.append(field.getProperty("last4"));
                for (; iStringLength > 4; iStringLength--)
                {
                    sb.insert(0, "*");
                }
                string = sb.toString();
            }            
            return string;
        }

    }
}
