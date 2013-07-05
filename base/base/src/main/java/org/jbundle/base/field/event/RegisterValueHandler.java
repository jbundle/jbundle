/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)FieldListener.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.util.UserProperties;
import org.jbundle.model.PropertyOwner;
import org.jbundle.model.screen.ComponentParent;


/**
 * Save this field's value in the registration database.
 * Retrieve the initial value on init and write to the registration DB
 * on change.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class RegisterValueHandler extends FieldListener
{

    /**
     * Constructor.
     */
    public RegisterValueHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param field The field to add this handler to (usually pass null and call addListener()).
     */
    public RegisterValueHandler(BaseField field)
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
    }
    /**
     * Set the field that owns this handler.
     * @param owner The field this listener was added to (or null if being removed).
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (owner != null)
            this.retrieveValue();
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
     * @return true If the value was retrieved from the registration database.
     */
    public boolean retrieveValue()
    {
        PropertyOwner pRegistration = this.retrieveUserProperties();
        if (pRegistration != null) if (pRegistration instanceof UserProperties)
            return ((UserProperties)pRegistration).retrieveField(this.getOwner());
        return false;
    }
    /**
     * Save the current value of this field to the registration database.
     */
    public void saveValue()
    {
        PropertyOwner pRegistration = this.retrieveUserProperties();
        if (pRegistration != null) if (pRegistration instanceof UserProperties)
            ((UserProperties)pRegistration).saveField(this.getOwner());
    }
    /**
     * Get the owner of this property key.
     * The registration key is the screen's key.
     * The property key is the field name.
     * @return The owner of this property key.
     */
    public PropertyOwner retrieveUserProperties()
    {
        Record record = this.getOwner().getRecord();
        ComponentParent screen = null;
        if (record.getRecordOwner() instanceof ComponentParent)
            screen = (ComponentParent)record.getRecordOwner();
        if (screen != null)
            return screen.retrieveUserProperties();   // Return the registration key
        else
            return null;                            // Must have a screen for this to work
    }
}
