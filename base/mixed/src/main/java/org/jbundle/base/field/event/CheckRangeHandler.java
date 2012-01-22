/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)CheckRangeHandler.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.text.MessageFormat;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.NumberField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ResourceConstants;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.model.Task;
import org.jbundle.thin.base.screen.BaseApplet;


/**
 * Make sure this number is in this range.
 * This listener only responds to a screen move.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class CheckRangeHandler extends FieldScratchHandler
{
    /**
     * Start of range (inclusive).
     */
    double m_dStartRange = 0;
    /**
     * End of range (inclusive).
     */
    double m_dEndRange = 0;
    /**
     * Too small error message key.
     */
    public static final String TOO_SMALL = "Too Small";
    /**
     * Too large error message key.
     */
    public static final String TOO_LARGE = "Too Large";

    /**
     * Constructor.
     */
    public CheckRangeHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param dStartRange Start of range (inclusive).
     * @param dEndRange End of range (inclusive).
     */
    public CheckRangeHandler(int dStartRange, int dEndRange)
    {
        this();
        this.init(null, dStartRange, dEndRange);
    }
    /**
     * Constructor.
     * @param dStartRange Start of range (inclusive).
     * @param dEndRange End of range (inclusive).
     */
    public CheckRangeHandler(double dStartRange, double dEndRange)
    {
        this();
        this.init(null, dStartRange, dEndRange);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param dStartRange Start of range (inclusive).
     * @param dEndRange End of range (inclusive).
     */
    public void init(BaseField field, double dStartRange, double dEndRange)
    {
        super.init(field);
        m_dStartRange = dStartRange;
        m_dEndRange = dEndRange;

        m_bScreenMove = true;
        m_bInitMove = false;
        m_bReadMove = false;
    }
    /**
     * Set this cloned listener to the same state at this listener.
     * @param field The field this new listener will be added to.
     * @param The new listener to sync to this.
     * @param Has the init method been called?
     * @return True if I called init.
     */
    public boolean syncClonedListener(BaseField field, FieldListener listener, boolean bInitCalled)
    {
        if (!bInitCalled)
            ((CheckRangeHandler)listener).init(null, m_dStartRange, m_dEndRange);
        return super.syncClonedListener(field, listener, true);
    }
    /**
     * Move the physical binary data to this field.
     * If this value is out of range, return an error.
     * @param objData the raw data to set the basefield to.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int doSetData(Object data, boolean bDisplayOption, int iMoveMode)
    {
		int iErrorCode = DBConstants.NORMAL_RETURN;
		if (data != null)
		{
		    iErrorCode = this.getFieldCopy().setData(data, DBConstants.DONT_DISPLAY, DBConstants.READ_MOVE);
		    if (iErrorCode != DBConstants.NORMAL_RETURN)
		        return iErrorCode; // Never!
		    if (!this.getFieldCopy().equals(this.getOwner()))
		    {   // Only check if the value changes
		        // Get this value
		        double dValue = ((NumberField)this.getFieldCopy()).getValue();
		        // Check the range
		        Task task = null;
		        BaseApplication application = null;
		        if (this.getOwner().getRecord().getRecordOwner() != null)
		            task = this.getOwner().getRecord().getRecordOwner().getTask();
		        if (task != null)
		            application = (BaseApplication)task.getApplication();
		        if (application == null)
		            application = (BaseApplication)BaseApplet.getSharedInstance().getApplication();
		        if (m_dStartRange != Double.MIN_VALUE) if (dValue < m_dStartRange)
		            return task.setLastError(MessageFormat.format(application.getResources(ResourceConstants.ERROR_RESOURCE, true).getString(TOO_SMALL), m_dStartRange));
		        if (m_dEndRange != Double.MAX_VALUE) if (dValue > m_dEndRange)
		            return task.setLastError(MessageFormat.format(application.getResources(ResourceConstants.ERROR_RESOURCE, true).getString(TOO_LARGE), m_dEndRange));
		    }
		}
		return super.doSetData(data, bDisplayOption, iMoveMode);    // Move; it's okay
    }
}
