/**
 *  @(#)TourTypeField.
 *  Copyright © 2007 tourapp.com. All rights reserved.
 */
package org.jbundle.base.field;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.field.convert.BitConverter;
import org.jbundle.base.field.convert.FieldDescConverter;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.GridScreen;
import org.jbundle.base.screen.model.SStaticString;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Converter;


/**
 *  BitReferenceField - Reference multiple table selections depending on the bits set in this field.
 */
public class BitReferenceField extends RecordReferenceField
{
    private static final long serialVersionUID = 1L;

    public static final Integer ALL_TABLES = new Integer(Integer.MAX_VALUE | Integer.MIN_VALUE);

    /**
     * Default constructor.
     */
    public BitReferenceField()
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
    public BitReferenceField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        if (strDefault == null)
            strDefault = ALL_TABLES;
        super.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Get (or make) the current record for this reference.
     */
    public Record makeReferenceRecord(RecordOwner screen)
    {
        return null;    // Override this
    }
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
        if (targetScreen instanceof GridScreen)
            return super.setupDefaultView(itsLocation, targetScreen, converter, iDisplayFieldDesc);
        Record record = this.makeReferenceRecord();
        
        ScreenField screenField = null;
        new SStaticString(itsLocation, targetScreen, DBConstants.BLANK);    // This sets the location of the other fields
        String strDisplay = converter.getFieldDesc();
        if ((strDisplay != null) && (strDisplay.length() > 0))
        {
            ScreenLocation descLocation = targetScreen.getNextLocation(ScreenConstants.FIELD_DESC, ScreenConstants.DONT_SET_ANCHOR);
            new SStaticString(descLocation, targetScreen, strDisplay);
        }
        
        try {
            record.close();
            while (record.hasNext())    // 0 = First Day -> 6 = Last Day of Week
            {
                record.next();
                Converter dayConverter = converter;
                String strWeek = record.getField(record.getDefaultDisplayFieldSeq()).toString();
                if (strWeek.length() > 0)
                    dayConverter = new FieldDescConverter(dayConverter, strWeek);
                int sBitPosition = (int)record.getCounterField().getValue();
                m_iBitsToCheck |= 1 << sBitPosition;
                dayConverter = new BitConverter(dayConverter, sBitPosition, true, true);
                itsLocation = targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST_CHECKBOX, ScreenConstants.DONT_SET_ANCHOR);
                screenField = (ScreenField)dayConverter.setupDefaultView(itsLocation, targetScreen, iDisplayFieldDesc);
            }
        } catch (DBException e) {
            e.printStackTrace();
        }
        return screenField;
    }
    int m_iBitsToCheck = 0;
    /**
     * Mask of valid bits.
     */
    public int getBitsToCheck()
    {
        if (m_iBitsToCheck == 0)
        {
            Record record = this.makeReferenceRecord();
            try {
                record.close();
                while (record.hasNext())    // 0 = First Day -> 6 = Last Day of Week
                {
                    record.next();
                    int sBitPosition = (int)record.getCounterField().getValue();
                    m_iBitsToCheck |= 1 << sBitPosition;
                }
            } catch (DBException e) {
                e.printStackTrace();
            }
        }
        return m_iBitsToCheck;
    }
}
