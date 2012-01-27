/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.event;

/**
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.BaseListener;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.field.event.DisableOnFieldHandler;
import org.jbundle.base.field.event.FieldRemoveBOnCloseHandler;
import org.jbundle.base.field.event.MainReadOnlyHandler;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.screen.ScreenComponent;

/**
 * Enable target field on valid and disable on new.
 */
public class EnableOnValidHandler extends FileListener
{
    public static final boolean ENABLE_ON_VALID = true;
    public static final boolean ENABLE_ON_NEW = true;
    public static final boolean DISABLE_ON_VALID = false;
    public static final boolean DISABLE_ON_NEW = false;

    protected int m_iFieldSeq = -1;
    /**
     * Target field to enable/disable.
     */
    protected BaseField m_fldTarget = null;
    /**
     * Screen field to enable/disable.
     */
    protected ScreenComponent m_sField = null;
    protected boolean m_bEnableOnValid = true;
    protected boolean m_bEnableOnNew = true;

    /**
     * Constructor.
     */
    public EnableOnValidHandler()
    {
        super();
    }
    /**
     * This constructor enable/disables ALL non-unique key fields.
     * @param bEnbleOnValid Enable/disable the fields on valid.
     * @param bEnableOnNew Enable/disable the fields on new.
     */
    public EnableOnValidHandler(boolean bEnableOnValid, boolean bEnableOnNew)
    {
        this();
        this.init(null, null, -1, bEnableOnValid, bEnableOnNew, null);
    }
    /**
     * This constructor enable/disables ALL non-unique key fields.
     * @param bEnbleOnValid Enable/disable the fields on valid.
     * @param bEnableOnNew Enable/disable the fields on new.
     */
    public EnableOnValidHandler(int iFieldSeq, boolean bEnableOnValid, boolean bEnableOnNew)
    {
        this();
        this.init(null, null, iFieldSeq, bEnableOnValid, bEnableOnNew, null);
    }
    /**
     * This constructor enable/disables ALL non-unique key fields.
     * @param bEnbleOnValid Enable/disable the fields on valid.
     * @param bEnableOnNew Enable/disable the fields on new.
     */
    public EnableOnValidHandler(BaseField field, boolean bEnableOnValid, boolean bEnableOnNew)
    {
        this();
        this.init(null, field, -1, bEnableOnValid, bEnableOnNew, null);
    }
    /**
     * Constructor.
     * @param field Target field.
     * @param bEnbleOnValid Enable/disable the fields on valid.
     * @param bEnableOnNew Enable/disable the fields on new.
     * @param flagField If this flag is true, do the opposite enable/disable.
     */
    public EnableOnValidHandler(ScreenComponent sField, boolean bEnableOnValid, boolean bEnableOnNew)
    {
        this();
        this.init(null, null, -1, bEnableOnValid, bEnableOnNew, sField);
    }
    /**
     * Constructor.
     * @param record My owner (usually passed as null, and set on addListener in setOwner()).
     * @param field Target field.
     * @param iFieldSeq Target field.
     * @param bEnbleOnValid Enable/disable the fields on valid.
     * @param bEnableOnNew Enable/disable the fields on new.
     * @param flagField If this flag is true, do the opposite enable/disable.
     */
    public void init(Record record, BaseField field, int iFieldSeq, boolean bEnableOnValid, boolean bEnableOnNew, ScreenComponent sField)
    {
        m_iFieldSeq = iFieldSeq;
        m_fldTarget = field;
        m_sField = sField;
        m_bEnableOnValid = bEnableOnValid;
        m_bEnableOnNew = bEnableOnNew;
        super.init(record);
    }
    /**
     * Creates a new object of the same class as this object.
     * Clone is not implemented for this listener.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        return null;
    }
    /**
     * Set the field or file that owns this listener.
     * @param owner My owner.
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner == null)
            return;
        if (m_iFieldSeq != -1)
            m_fldTarget = this.getOwner().getField(m_iFieldSeq);
        if (m_fldTarget != null) if (m_fldTarget.getRecord() != this.getOwner())    // If field is not in this file, remember to remove it
            m_fldTarget.addListener(new FieldRemoveBOnCloseHandler(this));
        if ((this.getOwner().getEditMode() == DBConstants.EDIT_CURRENT) || (this.getOwner().getEditMode() == DBConstants.EDIT_IN_PROGRESS))
            this.setEnabled(m_bEnableOnValid);
        if (this.getOwner().getEditMode() == DBConstants.EDIT_ADD)
            this.setEnabled(m_bEnableOnNew);
    }
    /**
     * Called when a valid record is read from the table/query.
     * Enables or disables the target field(s).
     * @param bDisplayOption If true, display any changes.
     */
    public void doValidRecord(boolean bDisplayOption) // Init this field override for other value
    {
        this.setEnabled(m_bEnableOnValid);
        super.doValidRecord(bDisplayOption);
    }
    /**
     * Called when a new blank record is required for the table/query.
     * Enables or disables the target field(s).
     * @param bDisplayOption If true, display any changes.
     */
    public void doNewRecord(boolean bDisplayOption)   // Init this field override for other value
    {
        this.setEnabled(m_bEnableOnNew);
        super.doNewRecord(bDisplayOption);
    }
    /**
     * Do the actual enable/disable of the target field(s).
     * @param bEnableFlag If true, enable.
     */
    public void setEnabled(boolean bEnableFlag)   // Init this field override for other value
    {
        if (m_fldTarget != null)
            m_fldTarget.setEnabled(bEnableFlag);
        else if (m_sField != null)
            m_sField.setEnabled(bEnableFlag);
        else
        {   // No field means enable/disable the key fields.
            Record table = this.getOwner();
            boolean bDisableOnFound = false;
            int iLastField = table.getFieldCount() + DBConstants.MAIN_FIELD - 1;
            for (int iFieldSeq = DBConstants.MAIN_FIELD; iFieldSeq <= iLastField; iFieldSeq++)
            {
                BaseField field = table.getField(iFieldSeq);
                BaseListener listener = field.getListener(MainReadOnlyHandler.class.getName());
                if (listener == null) // Don't touch if this has a read second listener on it!
                    field.setEnabled(bEnableFlag);
                if (field.getListener(DisableOnFieldHandler.class.getName()) != null)
                    bDisableOnFound = true;
            }
            if (bDisableOnFound)
            {   // These field listeners need to be notified that their fields have been enabled/disabled
                for (int iFieldSeq = DBConstants.MAIN_FIELD; iFieldSeq <= iLastField; iFieldSeq++)
                {
                    BaseField field = table.getField(iFieldSeq);
                    DisableOnFieldHandler listener = (DisableOnFieldHandler)field.getListener(DisableOnFieldHandler.class.getName());
                    if (listener != null)
                        listener.fieldChanged(DBConstants.DISPLAY, DBConstants.SCREEN_MOVE);    // I can do this because I know that this listener will not modify anything.
                }
                
            }
        }
    }
}
