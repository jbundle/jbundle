/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)FieldListener.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.Utility;
import org.jbundle.model.util.Util;


/**
 * Save this field's value in the screen URL, so it can be restored on back.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class StickyValueHandler extends FieldListener
{
    /*
     * Save this so I can re-push the history on free.
     */
    private BasePanel m_recordOwnerCache = null;

    /**
     * Constructor.
     */
    public StickyValueHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param field The field to add this handler to (usually pass null and call addListener()).
     */
    public StickyValueHandler(BaseField field)
    {
        this();
        this.init(field);
    }
    /**
     * Constructor.
     * @param field The field to add this handler to (usually pass null and call addListener()).
     */
    public void init(BaseField field)
    {
        super.init(field);
        m_bInitMove = true;     // Only respond to init!
        m_bReadMove = false;
        m_bScreenMove = true;
        m_recordOwnerCache = null;
    }
    /**
     * Set the field that owns this handler.
     * @param owner The field this listener was added to (or null if being removed).
     */
    public void setOwner(ListenerOwner owner)
    {
        if (owner == null)
        {
            this.saveValue(m_recordOwnerCache);
            m_recordOwnerCache = null;
        }
        super.setOwner(owner);
        if (owner != null)
            this.retrieveValue();
    }
    /**
     * Creates a new object of the same class as this object.
     * @param field The field to add the new cloned behavior to.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone(BaseField field) throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException(); // For now
    }
    /**
     * The Field has Changed.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        int iErrorCode = super.fieldChanged(bDisplayOption, iMoveMode);
        if (iErrorCode != DBConstants.NORMAL_RETURN)
            return iErrorCode;
        if (iMoveMode == DBConstants.INIT_MOVE)
            this.retrieveValue();
        if (iMoveMode == DBConstants.SCREEN_MOVE)
            this.saveValue();
        return iErrorCode;
    }
    /**
     * Retrieve the value of this field in the registration database, as set the field to this value.
     */
    public void retrieveValue()
    {
        BaseField field = this.getOwner();
        RecordOwner recordOwner = field.getRecord().getRecordOwner();
        if (recordOwner instanceof BasePanel)
        {
            String strValue = recordOwner.getProperty(field.getFieldName());
            if (strValue != null)
            {
                boolean[] rgbEnabled = field.setEnableListeners(false);
                field.setString(strValue, DBConstants.DISPLAY, DBConstants.INIT_MOVE);
                field.setEnableListeners(rgbEnabled);
            }
            m_recordOwnerCache = (BasePanel)recordOwner;
        }
    }
    /**
     * Save the current value of this field to the registration database.
     */
    public void saveValue()
    {
        BaseField field = this.getOwner();
        RecordOwner recordOwner = field.getRecord().getRecordOwner();
        if (recordOwner instanceof BasePanel)
            this.saveValue((BasePanel)recordOwner);
    }
    /**
     * Save the current value of this field to the registration database.
     * and change the URL on the push-down stack to take this into consideration.
     */
    public void saveValue(BasePanel recordOwner)
    {
        if (recordOwner != null)
        {
            BaseField field = this.getOwner();
            String strCommand = ((BasePanel)recordOwner).getParentScreen().popHistory(1, false);
            if (m_recordOwnerCache != null)
                if (strCommand != null)
                    if (strCommand.indexOf(m_recordOwnerCache.getClass().getName()) != -1)
            {   // Yes this is the command to open this window
                Map<String,Object> properties = new Hashtable<String,Object>();
                Util.parseArgs(properties, strCommand);
                properties.put(field.getFieldName(), field.toString());
                strCommand = Utility.propertiesToURL(null, properties);
            }
            ((BasePanel)recordOwner).getParentScreen().pushHistory(strCommand, false);
        }
    }
}
