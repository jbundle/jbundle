/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)SMenuButton.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Converter;


/**
 * Implements a button to be used as an item in menus.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class SMenuButton extends SCannedBox
{
    /**
     * The description text is an optional text field to be displayed below the button in a menu.
     */
    protected String m_strDescText = null;

    /**
     * Constructor.
     */
    public SMenuButton()
    {
        super();
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param strValue The value to set the field on button press.
     * @param strDesc The description of this button.
     * @param strImage The image filename for this button.
     * @param strCommand The command to send on button press.
     * @param strToolTip The tooltip for this button.
     * @param strText The optional text field (can be HTML) to display below the menu button.
     */
    public SMenuButton(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, String strValue, String strDesc, String strImage, String strCommand, String strToolTip, String strText)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, strValue, strDesc, strImage, strCommand, strToolTip, strText);
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param strValue The value to set the field on button press.
     * @param strDesc The description of this button.
     * @param strImage The image filename for this button.
     * @param strCommand The command to send on button press.
     * @param strToolTip The tooltip for this button.
     * @param strText The optional text field (can be HTML) to display below the menu button.
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, String strValue, String strDesc, String strImage, String strCommand, String strToolTip, String strText)
    {
        m_strDescText = strText;
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, strValue, strDesc, strImage, strCommand, strToolTip, null, null);
    }
    /**
     * Get the text string to display below the button.
     * @return The text string.
     */
    public String getDescText()
    {
        return m_strDescText;
    }
}
