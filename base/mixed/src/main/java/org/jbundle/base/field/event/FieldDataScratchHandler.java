/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)FieldScratchHandler.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.PropertiesField;
import org.jbundle.base.model.DBConstants;

/**
 * This listener saves a copy of the field's original value.
 * This can be useful if you need to find out what changes have been made.
 * @see PropertiesField
 * @version 1.0.0
 * @author    Don Corley
 */
public class FieldDataScratchHandler extends FieldListener
{
    /**
     * A cloned copy of the field to use as a scratch area.
     */
    protected Object m_objOriginalData = null;
    /**
     * Force this to be always enabled?
     */
    protected boolean m_bAlwaysEnabled = false;
    /**
     * 
     */
    protected boolean m_bChangeDataOnRefresh = true;
    /**
     * Constructor.
     */
    public FieldDataScratchHandler()
    {
        super();
        m_objOriginalData = null;
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public FieldDataScratchHandler(BaseField field)
    {
        this();
        this.init(field, true);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public FieldDataScratchHandler(BaseField field, boolean bChangeDataOnRefresh)
    {
        this();
        this.init(field, bChangeDataOnRefresh);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     */
    public void init(BaseField field, boolean bChangeDataOnRefresh)
    {
        super.init(field);
        m_objOriginalData = null;
        m_bAlwaysEnabled = false;
        m_bChangeDataOnRefresh = bChangeDataOnRefresh;
        this.setRespondsToMode(DBConstants.SCREEN_MOVE, false);
    }
    /**
     * Free this listener (and the field copy).
     */
    public void free()
    {
        super.free();
    }
    /**
     * Is this listener enabled?
     * This is different from isEnabled in that isEnabled determines is the listener
     * is enabled taking in account the environment (ie., Am I enabled in the SLAVE space?)
     * @return true if enabled.
     */
    public boolean isEnabled()
    {
        if (m_bAlwaysEnabled)
            return true;    // Forced to always be enabled
        else
            return super.isEnabled();
    }
    /*
    * The Field has Changed.
    * Don't need to call inherited.
    * @param bDisplayOption If true, display the change.
    * @param iMoveMode The type of move being done (init/read/screen).
    * @return The error code (or NORMAL_RETURN if okay).
    */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
    	if (iMoveMode != DBConstants.SCREEN_MOVE)	// This is necessary if an override change the repondsTo.
    	    // Also, if just refreshing the record, don't change this if set
    	    if ((m_bChangeDataOnRefresh) || ((this.getOwner().getRecord().getOpenMode() & DBConstants.OPEN_DONT_CHANGE_CURRENT_LOCK_TYPE) != DBConstants.OPEN_DONT_CHANGE_CURRENT_LOCK_TYPE))
    	        if ((this.getOwner().getRecord().getOpenMode() & DBConstants.OPEN_DONT_UPDATE_LAST_READ) != DBConstants.OPEN_DONT_UPDATE_LAST_READ)    // This is a special case where I want a subsequent write to get an error if changed 
    	            this.setOriginalData(this.getOwner().getData());
    	return super.fieldChanged(bDisplayOption, iMoveMode);
    }
    /**
     * Get the original data value.
     * @return
     */
    public Object getOriginalData()
    {
        return m_objOriginalData;
    }
    /**
     * Set the original data value.
     * @return
     */
    public void setOriginalData(Object originalData)
    {
        m_objOriginalData = originalData;
    }
    /**
     * Set the original data value.
     * @return
     */
    public boolean setAlwaysEnabled(boolean bAlwaysEnabled)
    {
    	boolean bOldAlwaysEnabled = m_bAlwaysEnabled;
        m_bAlwaysEnabled = bAlwaysEnabled;
        return bOldAlwaysEnabled;
    }
}
