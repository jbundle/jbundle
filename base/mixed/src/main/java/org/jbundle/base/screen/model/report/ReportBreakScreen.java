/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.report;

/**
* @(#)Screen.java 0.00 12-Feb-97 Don Corley
*
* Copyright (c) 2009 tourapp.com. All Rights Reserved.
*   don@tourgeek.com
*/
import java.io.PrintWriter;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.CounterField;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.HtmlConstants;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Converter;

/**
 * This is the base for any report heading that requires a control break.
 */
public class ReportBreakScreen extends HeadingScreen
{
    /**
     * Constructor.
     */
    public ReportBreakScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public ReportBreakScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc,  properties);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Display this sub-control in html input format?
     * @param iPrintOptions The view specific print options.
     * @return True if this sub-control is printable.
     */
    public boolean isPrintableControl(ScreenField sField, int iPrintOptions)
    {
        // Override this to break
        if ((sField == null) || (sField == this))
        {       // Asking about this control
            int iDisplayOptions = m_iDisplayFieldDesc & (HtmlConstants.HEADING_SCREEN | HtmlConstants.FOOTING_SCREEN);
            if (iPrintOptions != 0)
                if ((iPrintOptions & iDisplayOptions) == iDisplayOptions)
                    return true;    // detail screens are printed as a sub-screen.
        }
        return super.isPrintableControl(sField, iPrintOptions);
    }
    /**
     * Print this field's data in XML format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iPrintOptions)
    {
        this.setLastBreak(this.isBreak());
        if (!this.isLastBreak())
            return false;
        if ((iPrintOptions & HtmlConstants.FOOTING_SCREEN) != HtmlConstants.FOOTING_SCREEN)
            this.setLastBreak(false);     // For footers only

        boolean bInputFound = super.printData(out, iPrintOptions);
        
        return bInputFound;
    }
    /**
     * Was the last check a break?
     */
    public void setLastBreak(boolean bIsLastBreak)
    {
        m_bIsLastBreak = bIsLastBreak;
    }
    /**
     * Was the last check a break?
     */
    public boolean isLastBreak()
    {
        return m_bIsLastBreak;
    }
    /**
     *
     */
    protected boolean m_bIsLastBreak = false;
    /**
     * First time constant.
     */
    protected Object INITIAL_VALUE = this;
    /**
     * The value of the control value on the last pass.
     */
    protected Object m_objLastValue = INITIAL_VALUE;
    
    /**
     * Is this a control break?
     * @return True if it is a break.
     */
    public boolean isBreak()
    {
        if (this.getMainRecord() != null)
            if ((this.getMainRecord().getEditMode() != DBConstants.EDIT_CURRENT)
                && (this.getMainRecord().getEditMode() != DBConstants.EDIT_IN_PROGRESS))
        {       // End of file
            if ((m_iDisplayFieldDesc & HtmlConstants.FOOTING_SCREEN) == HtmlConstants.FOOTING_SCREEN)
                return true;    // EOF = always break on footing
            if ((m_iDisplayFieldDesc & HtmlConstants.HEADING_SCREEN) == HtmlConstants.HEADING_SCREEN)
                return false;   // EOF = always break on heading
        }
        Object objValue = this.getBreakValue();
        if (m_objLastValue != INITIAL_VALUE)
            if ((objValue == m_objLastValue)
                || ((objValue != null) && (objValue.equals(m_objLastValue))))
                    return false;    // Not a break
        boolean bChange = true;
        if (m_objLastValue == INITIAL_VALUE)
        {       // End of file
            if ((m_iDisplayFieldDesc & HtmlConstants.FOOTING_SCREEN) == HtmlConstants.FOOTING_SCREEN)
                bChange = false;    // Never on first time
        }
        m_objLastValue = objValue;
        return bChange;   // Is a break
    }
    /**
     * Get the value to break on.
     * By default, use the first field of the current key (as long as it isn't the counter).
     * @return The break value.
     */
    public Object getBreakValue()
    {
        BaseField field = null;
        if (this.getMainRecord() != null)
            field = this.getMainRecord().getKeyArea().getField(0);
        if (field != null)
            if (!(field instanceof CounterField))
                return field.getData();
        return INITIAL_VALUE;   // Override this
    }
}
