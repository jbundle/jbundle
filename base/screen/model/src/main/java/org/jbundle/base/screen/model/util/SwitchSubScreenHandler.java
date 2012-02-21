/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.util;

/**
 * @(#)SwitchSubScreenHandler.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.Map;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.ListenerOwner;
import org.jbundle.base.field.NumberField;
import org.jbundle.base.field.event.FieldListener;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.BaseScreen;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.BaseAppletReference;


/**
 * Switch the sub screen on field change.
 * To make this listener work, you need to override this class and
 * implement the getSubScreen() method.
 */
public class SwitchSubScreenHandler extends FieldListener
{
    /**
     * Keep track of the current screen number displayed.
     */
    protected int m_iCurrentScreenNo = -1;
    /**
     * Screen's parent.
     */
    protected BasePanel m_screenParent = null;
    /**
     * Screen's seq in parent (Do not use sub, because it may change).
     */
    protected int m_iScreenSeq = -1;

    /**
     * Constructor.
     */
    public SwitchSubScreenHandler()
    {
        super();
    }
    /**
     * Constructor.
     */
    public SwitchSubScreenHandler(BaseField field, BasePanel screenParent, BasePanel subScreen)
    {
        this();
        this.init(field, screenParent, subScreen);
    }
    /**
     * Constructor.
     * @param field The basefield owner of this listener (usually null and set on setOwner()).
     * @param screenParent The parent screen of this sub-screen.
     * @param subScreen The current sub-screen.
     */
    public void init(BaseField field, BasePanel screenParent, BasePanel subScreen)
    {
        super.init(field);
        m_iCurrentScreenNo = -1;
        m_screenParent = screenParent;
        this.setCurrentSubScreen(subScreen);
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
        throw new CloneNotSupportedException(); // Not supported
    }
    /**
     * Set the field that owns this listener.
     * @owner The field that this listener is being added to (if null, this listener is being removed).
     */
    public void setOwner(ListenerOwner owner)
    {
        super.setOwner(owner);
        if (this.getOwner() != null) if (!this.getOwner().isNull())
            // Display the screen that is supposed to be up now
            this.fieldChanged(DBConstants.DISPLAY, DBConstants.INIT_MOVE);
    }
    /**
     * The Field has Changed.
     * Get the value of this listener's field and setup the new sub-screen.
     * @param bDisplayOption If true, display the change.
     * @param iMoveMode The type of move being done (init/read/screen).
     * @return The error code (or NORMAL_RETURN if okay).
     * Change the sub-screen to coorespond to this screen number.
     */
    public int fieldChanged(boolean bDisplayOption, int moveMode)
    {
        int iScreenNo = (int)((NumberField)m_owner).getValue();
        if ((iScreenNo == -1) || (iScreenNo == m_iCurrentScreenNo))
            return DBConstants.NORMAL_RETURN;
        ScreenLocation screenLocation = null;
        // First, find the current sub-screen
        this.setCurrentSubScreen(null);     // Make your best guess as to the old sub-screen
        // Display wait cursor
        BaseAppletReference applet = null;
        if (m_screenParent.getTask() instanceof BaseAppletReference)
        	applet = (BaseAppletReference)m_screenParent.getTask();
        Object oldCursor = null;
        if (applet != null)
        	oldCursor = applet.setStatus(DBConstants.WAIT, applet, null);

        ScreenField sField = m_screenParent.getSField(m_iScreenSeq);
        if ((sField != null) && (sField instanceof BaseScreen))
        { // First, get rid of the old screen
            screenLocation = sField.getScreenLocation();
            m_screenParent = sField.getParentScreen();
            sField.free();
            sField = null;
            if (m_screenParent == null)
                if (this.getOwner().getComponent(0) instanceof ScreenField)    // Always
                m_screenParent = ((ScreenField)this.getOwner().getComponent(0)).getParentScreen();
        }
        if (screenLocation == null)
            screenLocation = m_screenParent.getNextLocation(ScreenConstants.FLUSH_LEFT, ScreenConstants.FILL_REMAINDER);
        sField = this.getSubScreen(m_screenParent, screenLocation, null, iScreenNo);
        if (applet != null)
            applet.setStatus(0, applet, oldCursor);
        if (sField == null)
            m_iCurrentScreenNo = -1;
        else
            m_iCurrentScreenNo = iScreenNo;  
        return DBConstants.NORMAL_RETURN;
    }
    /**
     * Set up the information so I can find this sub-screen.
     * @return The current sub-screen (or null if none).
     */
    public ScreenField getSubScreen()
    {
        this.setCurrentSubScreen(null);
        if (m_iScreenSeq != -1)
            return m_screenParent.getSField(m_iScreenSeq);
        else
            return null;
    }
    /**
     * Build this sub-screen.
     * @param parentScreen The parent screen.
     * @param screenLocation The location to place the new sub-screen (null = same as current sub-screen).
     * @param iScreenNo The sub-screen to build.
     */
    public BasePanel getSubScreen(BasePanel parentScreen, ScreenLocation screenLocation, Map<String, Object> properties, int screenNo)
    {
        return null;        // Must override
    }
    /**
     * Set up m_iScreenSeq so I can find this sub-screen.
     * @param subScreen The current sub-screen.
     */
    public void setCurrentSubScreen(BasePanel subScreen)
    {
        m_screenParent = null;      // Screen's parent
        m_iScreenSeq = -1;
        if (subScreen != null)
            m_screenParent = subScreen.getParentScreen();   // Screen's parent
        if (m_screenParent == null) if (this.getOwner() != null) if (this.getOwner().getRecord() != null)
            m_screenParent = (BaseScreen)this.getOwner().getRecord().getRecordOwner();
        if (m_screenParent == null) if (this.getOwner() != null) if (this.getOwner().getComponent(0) instanceof ScreenField)
            m_screenParent = ((ScreenField)this.getOwner().getComponent(0)).getParentScreen();
        if (m_screenParent == null)
            return;
        int iBestGuess = -1;
        for (m_iScreenSeq = 0; m_iScreenSeq < m_screenParent.getSFieldCount(); m_iScreenSeq++)
        {
            ScreenField sField = m_screenParent.getSField(m_iScreenSeq);
            if (sField == subScreen)
                return;     // Found (m_iScreenSeq is correct)
            if (sField instanceof BaseScreen)
                iBestGuess = m_iScreenSeq;
        }
        m_iScreenSeq = iBestGuess;
    }
    public void setCurrentScreenNo(int iCurrentScreenNo)
    {
        m_iCurrentScreenNo = iCurrentScreenNo;
    }
}
