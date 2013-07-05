/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.event;

/**
 * @(#)ChangeTitleOnChangeHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import javax.swing.JFrame;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenParent;
import org.jbundle.thin.base.db.Constants;


/**
 * Change the title of the frame when this field changes.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ChangeTitleOnChangeHandler extends FieldListener
{
    /**
     * If true, set the title to the value of this listener's field.
     */
    protected boolean m_bSetTitleToThisString = false;

    /**
     * Constructor.
     */
    public ChangeTitleOnChangeHandler()
    {
        super();
    }
    /**
     * Constructor.
     * @param bSetTitleToThisString If true, set the title to the value of this listener's field.
     */
    public ChangeTitleOnChangeHandler(boolean bSetTitleToThisString)
    {
        this();
        this.init(null, bSetTitleToThisString);
    }
    /**
     * Constructor.
     * @param owner The basefield owner of this listener (usually null and set on setOwner()).
     * @param bSetTitleToThisString If true, set the title to the value of this listener's field.
     */
    public void init(BaseField field, boolean bSetTitleToThisString)
    {
        super.init(field);
        m_bSetTitleToThisString = bSetTitleToThisString;
    }
    /*
     * The Field has Changed.
     * This method tries to get the new title for the screen and sets the new frame title.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        Record recDefault = this.getOwner().getRecord();
        ComponentParent screen = null;
        if (recDefault.getRecordOwner()instanceof ScreenParent)
            screen = (ComponentParent)recDefault.getRecordOwner();
        screen = screen.getRootScreen();
        String strScreenTitle = Constants.BLANK;
        if (m_bSetTitleToThisString)
            strScreenTitle = this.getOwner().getString();
        if (strScreenTitle.length() == 0)
            strScreenTitle = this.getTitle(screen);
        if (strScreenTitle.length() > 0)
        {
            Object pWnd = screen.getControl();
            JFrame pWndFrame = null;
            if (pWndFrame == null)
                pWndFrame = (JFrame)pWnd;
            if (pWndFrame != null)
                pWndFrame.setTitle(strScreenTitle);
        }
        return DBConstants.NORMAL_RETURN;
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
            ((ChangeTitleOnChangeHandler)listener).init(null, m_bSetTitleToThisString);
        return super.syncClonedListener(field, listener, true);
    }
    /**
     * Get the default title for this screen.
     * Calls the getTitle method of this screen.
     * @param screenMain The screen to get the title string from.
     * @return The returned title for this screen.
     */
    public String getTitle(ComponentParent screenMain)
    {
        String strScreenTitle = DBConstants.BLANK;
        if (screenMain != null)
            strScreenTitle = screenMain.getTitle();
        return strScreenTitle;
    }
}   
