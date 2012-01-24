/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)SStaticString.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.Map;

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Converter;

/**
 * A static text area.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SStaticString extends ScreenField
{
    /**
     * The value of this static string.
     */
    protected String m_StaticString = null;

    /**
     * Constructor.
     */
    public SStaticString()
    {
        super();
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param displayString The static string to display.
     */
    public SStaticString(ScreenLocation itsLocation, BasePanel parentScreen, String strDisplay)
    {
        this();
        m_StaticString = strDisplay;
        this.init(itsLocation, parentScreen, strDisplay);
        m_bIsFocusTarget = false; // Can't tab to a static item
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        if (properties != null)
            m_StaticString = (String)properties.get(ScreenModel.DISPLAY_STRING);
        if (m_StaticString == null)
            m_StaticString = DBConstants.BLANK;
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param displayString The static string to display.
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, String strDisplay)
    {
        m_StaticString = strDisplay;
        super.init(itsLocation, parentScreen, null, ScreenConstants.DONT_DISPLAY_DESC, null);
    }
    /**
     * Free.
     */
    public void free()
    {
        m_StaticString = null;
        super.free();
    }
    /**
     * Set the static string for this label.
     * @param string The static string.
     */
    public void setString(String string)
    {
        m_StaticString = string;
        if (this.getScreenFieldView().getControl() != null)
            this.getScreenFieldView().setComponentState(this.getScreenFieldView().getControl(), string);
    }
    /**
     * Get the static string for this label.
     * @return The static string.
     */
    public String getStaticString()
    {
        return m_StaticString;
    }
    /**
     * Enable or disable this control.
     * For StaticStrings, do not disable the physical control.
     * @param bEnable Ignored for static strings.
     */
    public void setEnabled(boolean enable)
    {
        m_bEnabled = enable;
    }
    /**
     * Is this a valid Html Input field?
     * Not if it isn't linked to a converter.
     * @return true if this can be an HTML input field.
     */
    public boolean isInputField()
    {
        return (this.getConverter() != null);
    }
}
