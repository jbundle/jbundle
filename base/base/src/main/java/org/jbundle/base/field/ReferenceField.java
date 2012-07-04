/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field;

/**
 * Reference to an object in another class.<p>
 *  Note: This field will only save references to BaseTable records.
 * NOTE: There is no automatic behaviors associated with this field. If you want the
 * secondary record to be read, you must add a listener to this field.
 */

import java.util.HashMap;
import java.util.Map;

import org.jbundle.base.db.KeyArea;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.ClearFieldReferenceOnCloseHandler;
import org.jbundle.base.db.event.MoveOnValidHandler;
import org.jbundle.base.field.convert.FieldConverter;
import org.jbundle.base.field.convert.FieldDescConverter;
import org.jbundle.base.field.event.FieldListener;
import org.jbundle.base.field.event.MainReadOnlyHandler;
import org.jbundle.base.field.event.MoveOnChangeHandler;
import org.jbundle.base.field.event.ReadSecondaryHandler;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.model.DBException;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.BaseAppletReference;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.GridScreenParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;


public class ReferenceField extends RecordReferenceField
{
	private static final long serialVersionUID = 1L;

    /**
     * ReferenceField constructor comment.
     */
    public ReferenceField()
    {
        super();
    }
    /**
     * ReferenceField constructor comment.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public ReferenceField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
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
    }
    /**
     * Move data to this field from another field.
     * If these isn't a reference record yet, figures out the reference record.
     * @param field The source field.
     * @return The error code (or NORMAL_RETURN).
     */
    public int moveFieldToThis(BaseField field, boolean bDisplayOption, int iMoveMode)
    {   // Save the record and datarecord.
        if (field == null)
            return DBConstants.NORMAL_RETURN;
        if (field instanceof CounterField)
        {
            if (this.getReferenceRecord(null, false) == null)
                if (field.getRecord().getListener(ClearFieldReferenceOnCloseHandler.class) == null) // While it is okay to have two fields reference on record, don't default it.
                    this.setReferenceRecord(field.getRecord());
        }
        else if (field instanceof ReferenceField)
        {
//x no           this.setReferenceRecord(((ReferenceField)field).getReferenceRecord());
        }
        else
            this.setReferenceRecord(null);
        return super.moveFieldToThis(field, bDisplayOption, iMoveMode);
    }
    /**
     * Get the record that this field references and make it current.
     * @return tour.db.Record Record pointing to the current data.
     */
    public Record getReference()
    {
        Record record = this.getReferenceRecord();
        if (record != null)
        {
            try   {
                if (this.getData() == null)
                {
                    record.addNew();
                    return record;
                }
                Object bookmark = this.getData();
                if ((record.getEditMode() == Constants.EDIT_IN_PROGRESS)
                    || (record.getEditMode() == Constants.EDIT_CURRENT))
                {
                    Object oldBookmark = record.getHandle(DBConstants.BOOKMARK_HANDLE);
                    if ((bookmark != null) && (bookmark.equals(oldBookmark)))
                        return record;  // This record is already current
                }
                record.setHandle(bookmark, DBConstants.BOOKMARK_HANDLE);
            } catch (DBException ex)    {
                ex.printStackTrace();
                record = null;
            }   
        } 
        return record;
    }
    /**
     * Get the record name that this field references.
     * @return String Name of the record.
     */
    public String getReferenceRecordName()
    {
        if (m_recordReference != null)
            return this.getReferenceRecord().getTableNames(false);
        else
        {   // This code just takes a guess
            if (this.getClass().getName().indexOf("Field") != -1)
                return this.getClass().getName().substring(Math.max(0, this.getClass().getName().lastIndexOf('.') + 1), this.getClass().getName().indexOf("Field"));
            else if (this.getFieldName(false, false).indexOf("ID") != -1)
                return this.getFieldName(false, false).substring(0, this.getFieldName(false, false).indexOf("ID"));
            else
                this.getFieldName(false, false);
        }
        return Constants.BLANK;   // Never
    }
    /**
     * Make this field a reference to the current object in this record info class.
     * @param record tour.db.Record The current record to set this field to reference.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setReference(Record record)
    {
        return this.setReference(record, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);
    }
    /**
     * Make this field a reference to the current object in this record info class.
     * @param record tour.db.Record The current record to set this field to reference.
     * @param bDisplayOption If true, display changes.
     * @param iMoveMode The move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setReference(Record record, boolean bDisplayOption, int iMoveMode)
    {
        return this.moveFieldToThis((BaseField)record.getCounterField(), bDisplayOption, iMoveMode);
    }
    /**
     * Set up the default screen control for this field.
     * By default, this is set to a popup.
     * @param itsLocation Location of this component on screen (ie., GridBagConstraint).
     * @param targetScreen Where to place this component (ie., Parent screen or GridBagLayout).
     * @param converter The converter to set the screenfield to.
     * @param iDisplayFieldDesc Display the label? (optional).
     * @return Return the component or ScreenField that is created for this field.
     */
    public ScreenComponent setupDefaultView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert converter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        // Add code here to setup the popup or lookup button
        Record record = this.getReferenceRecord();  // Get/make the record that describes the referenced class.
        if (record != null)
            return this.setupTablePopup(itsLocation, targetScreen, converter, iDisplayFieldDesc, record, record.getDefaultScreenKeyArea(), this.getDefaultDisplayFieldSeq(), true, false); 
        else
            return super.setupDefaultView(itsLocation, targetScreen, converter, iDisplayFieldDesc, properties);
    }
    /**
     * Enable/Disable the associated control(s).
     * @param bEnable If false, disable all this field's screen fields.
     */
    public void setEnabled(boolean bEnable)
    {
        super.setEnabled(bEnable);
        if (m_recordReference != null)
        {
            Record recReference = this.getReferenceRecord();
            for (int iIndex = 0 + 1; iIndex < recReference.getKeyAreaCount(); iIndex++)
            {
                KeyArea keyArea = recReference.getKeyArea(iIndex);
                BaseField fieldKey = keyArea.getField(DBConstants.MAIN_KEY_FIELD);
                if (fieldKey.getListener(MainReadOnlyHandler.class.getName()) != null)
                    fieldKey.setEnabled(bEnable);
            }
        }
    }
    /**
     * Synchronize this refernce field with this record.
     * Adds the behaviors to sync this field and the record.
     * Used for popup screenfields where the referencerecord has a detail to display on change.
     * @param record The reference record to synchronize.
     */
    public void syncReference(Record record)
    {
        this.setReferenceRecord(record);
        BaseField recordKeyField = (BaseField)record.getCounterField();
        if ((recordKeyField.isNull()) && (!this.isNull())
            && (!record.isModified())
                && (record.getEditMode() != DBConstants.EDIT_CURRENT)
                    && (record.getEditMode() != DBConstants.EDIT_IN_PROGRESS)
                     && ((record.getOpenMode() & DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY) == 0))
        {
            recordKeyField.moveFieldToThis(this, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);   // Start with this field's value if you arn't going to mess up the record
            recordKeyField.setModified(false);
        } 
        else
            this.setReference(record, DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);
            // If the screen field is changed, make sure the passed in header record's key field is changed to match.
        MoveOnChangeHandler listener = (MoveOnChangeHandler)this.getListener(MoveOnChangeHandler.class.getName());
        if ((listener == null)
            || (listener.getDestField() != recordKeyField))
                this.addListener(new MoveOnChangeHandler(recordKeyField, null));
            // If the record is externally selected, sync the record's key with the screenfield
        MoveOnValidHandler listener2 = (MoveOnValidHandler)record.getListener(MoveOnValidHandler.class.getName());
        if ((listener2 == null)
            || (listener2.getSourceField() != recordKeyField))
                record.addListener(new MoveOnValidHandler(this, recordKeyField));
        MainReadOnlyHandler listener3 = (MainReadOnlyHandler)recordKeyField.getListener(MainReadOnlyHandler.class.getName());
        if (listener3 == null)
            recordKeyField.addListener(new MainReadOnlyHandler(DBConstants.MAIN_KEY_AREA));
    }
    /**
     * Get the referenced record's ID given the code.
     * This method converts the code to the record ID by reading the secondary key of the referenced record.
     * @param strCode The code to convert (ie., "EMAIL_TYPE")
     * @return int The ID of the referenced record (or 0 if not found).
     */
    public int getIDFromCode(String strCode)
    {
        int iID = 0;
        try   {
            iID = Integer.parseInt(strCode);    // Special case - if an integer, just convert it.
        } catch (NumberFormatException ex)  {
            iID = 0;
        }
        if (iID == 0)
        {
            Record record = this.getReferenceRecord();
            if (record != null)
                iID = record.getIDFromCode(strCode);
        }
        return iID;
    }
    public static Object NONE_BUTTON = null;
    /**
     * Display NONE when the button is blank.
     */
    public class BlankButtonHandler extends FieldListener
    {
        public BlankButtonHandler()
        {
            super();
        }
        public BlankButtonHandler(BaseField owner)
        {
            this();
            this.init(owner);
        }
        public Object doGetData()
        {
            Object data = super.doGetData();
            if ((data == null) || (DBConstants.BLANK.equals(data)))
            {
                if (NONE_BUTTON == null)
                {
                    String NONE = "None";
                    if (getRecord().getTask() != null)
                    {
                        BaseAppletReference reference = null;
                        if (getRecord().getTask() instanceof BaseAppletReference)
                            reference = (BaseAppletReference)getRecord().getTask();
                        if (getRecord().getTask().getApplication() != null)
                            NONE_BUTTON = getRecord().getTask().getApplication().getResourceURL(Util.getImageFilename(NONE, "buttons"), reference);
                    }
                }
                return NONE_BUTTON;
            }
            return data;
        }
    };
    /**
     * Display a button that shows the icon from the current record in the secondary file.
     */
    public ScreenComponent setupIconView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert converter, int iDisplayFieldDesc, boolean bIncludeBlankOption)
    {
        ScreenComponent screenField = null;
        Record record = this.makeReferenceRecord();
        //  Set up the listener to read the current record on a valid main record
        
        ImageField fldDisplayFieldDesc = this.getIconField(record);
        if (fldDisplayFieldDesc.getListener(BlankButtonHandler.class) == null)
            fldDisplayFieldDesc.addListener(new BlankButtonHandler(null));
        if (fldDisplayFieldDesc != null)
        {    // The next two lines are so in GridScreen(s), the converter leads to this field, while it displays the fielddesc.
            FieldConverter fldDescConverter = new FieldDescConverter(fldDisplayFieldDesc, (Converter)converter);
            Map<String,Object> properties = new HashMap<String,Object>();
            properties.put(ScreenModel.IMAGE, ScreenModel.NONE);
            properties.put(ScreenModel.NEVER_DISABLE, Constants.TRUE);
            screenField = createScreenComponent(ScreenModel.BUTTON_BOX, itsLocation, targetScreen, fldDescConverter, iDisplayFieldDesc, properties);
            //?{
            //?    public void setEnabled(boolean bEnabled)
            //?    {
            //?        super.setEnabled(true); // Never disable
            //?    }
            //?};
            String strDisplay = converter.getFieldDesc();
            if (!(targetScreen instanceof GridScreenParent))
                if ((strDisplay != null) && (strDisplay.length() > 0))
            { // Since display string does not come with buttons
                ScreenLoc descLocation = targetScreen.getNextLocation(ScreenConstants.FIELD_DESC, ScreenConstants.DONT_SET_ANCHOR);
                properties = new HashMap<String,Object>();
                properties.put(ScreenModel.DISPLAY_STRING, strDisplay);
                createScreenComponent(ScreenModel.STATIC_STRING, descLocation, targetScreen, converter, iDisplayFieldDesc, properties);
            }
        }

        if (((targetScreen instanceof GridScreenParent)) || (iDisplayFieldDesc == ScreenConstants.DONT_DISPLAY_FIELD_DESC))
        { // If there is no popupbox to display the icon, I must explicitly read it.
            this.addListener(new ReadSecondaryHandler(fldDisplayFieldDesc.getRecord()));
        }
        return screenField;
    }
    /**
     * Get the IconField from this record.
     */
    public ImageField getIconField(Record record)
    {
        if (record == null)
            record = this.getReferenceRecord();
        for (int i = 0; i < record.getFieldCount(); i++)
        {
            BaseField field = record.getField(i);
            if (field instanceof ImageField)
                return (ImageField)field;
        }
        return null;    // No icon in this status record
    }
    /**
     * Add icon to popup.
     */
    public ScreenComponent setupPopupView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert converter, int iDisplayFieldDesc, boolean bIncludeBlankOption)
    {
        ScreenComponent screenField = null;
        Record record = this.makeReferenceRecord();
        //  Set up the listener to read the current record on a valid main record
        screenField = this.setupIconView(itsLocation, targetScreen, converter, iDisplayFieldDesc, bIncludeBlankOption);
        
        if ((!(targetScreen instanceof GridScreenParent)) && (iDisplayFieldDesc != ScreenConstants.DONT_DISPLAY_FIELD_DESC))
        {   // If it is not in a grid screen, add the description
            if (screenField != null)
            {
                itsLocation = targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR);
                iDisplayFieldDesc = ScreenConstants.DONT_DISPLAY_FIELD_DESC;
            }
            screenField = this.setupTablePopup(itsLocation, targetScreen, converter, iDisplayFieldDesc, record, -1, -1, bIncludeBlankOption, false);
        }
        
        return screenField;
    }
}
