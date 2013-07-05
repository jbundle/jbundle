/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)FieldReSelect.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.FileListener;
import org.jbundle.base.db.event.FileRemoveBOnCloseHandler;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.screen.GridComponent;
import org.jbundle.model.screen.GridScreenParent;

/**
 * Reselect a grid screen when this field changes.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class FieldReSelectHandler extends FieldListener
{
    /**
     * The grid screen to reselect on change.
     */
    protected GridScreenParent m_gridScreen = null;
    /**
     * The grid screen to reselect on change.
     */
    protected GridComponent m_sPopupBox = null;

    /**
     * Constructor.
     */
    public FieldReSelectHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param gridScreen The grid screen to reselect on change.
     */
    public FieldReSelectHandler(GridScreenParent gridScreen)
    { // For this to work right, the field needs a listener to re-select this file whenever it changes
        this();
        this.init(null, gridScreen, null);
    }
    /**
     * Constructor.
     * @param gridScreen The grid screen to reselect on change.
     */
    public FieldReSelectHandler(GridComponent sPopupBox)
    { // For this to work right, the field needs a listener to re-select this file whenever it changes
        this();
        this.init(null, null, sPopupBox);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param gridScreen The grid screen to reselect on change.
     */
    public void init(BaseField field, GridScreenParent gridScreen, GridComponent sPopupBox)
    {
        super.init(field);
        m_gridScreen = gridScreen;
        m_sPopupBox = sPopupBox;
        Record gridFile = null;
        if (gridScreen != null)
            gridFile = (Record)gridScreen.getMainRecord();
        if (gridFile != null)
        {   // Remove this listener when the file closes
            FileListener listener = new FileRemoveBOnCloseHandler(this);    // If this closes first, this will remove FileListener
            gridFile.addListener(listener);   // Remove this if you close the file first
        }
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
            ((FieldReSelectHandler)listener).init(null, m_gridScreen, m_sPopupBox);
        return super.syncClonedListener(field, listener, true);
    }
    /**
     * The Field has Changed.
     * Reselect the grid screen.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     * Reselect the records.
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        if (m_gridScreen != null)
            m_gridScreen.reSelectRecords();
        if (m_sPopupBox != null)
            m_sPopupBox.reSelectRecords();
        return DBConstants.NORMAL_RETURN;
    }
} 
