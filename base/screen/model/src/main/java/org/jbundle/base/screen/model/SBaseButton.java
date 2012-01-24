/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)SBaseButton.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.convert.CheckConverter;
import org.jbundle.base.field.convert.FieldDescConverter;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;


/**
 * The base class for all buttons and check boxes.
 *
 * @version 1.0.0
 * @author    Don Corley
 *
 * The information that can be passed to a button is as follows:
 *<ul>
 *  A Converter.
 *  Button text (defaults to converter.getFieldDesc()).
 *  Image filename. (defaults to command if text null)
 *  Command to send to controls (only if converter is null; default to text, then ImageFilename)
 *  Value to set converter to.
 *  Tooltip.
 *</ul>
 */
public class SBaseButton extends ScreenField
{
    /**
     * The converter this button is dependent upon.
     */
    protected Converter m_DependentConverter = null;
    /**
     * Image filename for this button.
     */
    protected String m_strImageButton = null;
    /**
     * The command to send to the controls when this button is pressed.
     */
    protected String m_strCommand = null;
    /**
     * The tooltip for this button.
     */
    protected String m_strToolTip = null;

    /**
     * Constructor.
     */
    public SBaseButton()
    {
        super();
    }
    /**
     * Constructor.
     */
    public SBaseButton(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, null, null, null, null, null);
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param strValue The value to set the field on button press.
     * @param strDesc The description of this button.
     */
    public SBaseButton(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, String strValue, String strDesc)
    {
        this();
        m_DependentConverter = null;
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, strValue, strDesc, null, null, null);
    }
    /**
     * Initialize.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, String strValue, String strDesc, String strImage, String strCommand, String strToolTip)
    {
        m_strImageButton = strImage;        // Image for this button
        m_strToolTip = strToolTip;
        Converter converter = null;
        if (strValue != null)
            converter = new CheckConverter(fieldConverter, strValue, strDesc, true);
        else if (strDesc != null)
            converter = new FieldDescConverter(fieldConverter, strDesc);
        if (converter != null)
        {
            if (fieldConverter == null)
                m_DependentConverter = converter; // Remember to remove this
            fieldConverter = converter;
        }
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, null);
    }
    /**
     * Free.
     */
    public void free()
    {
        if (m_DependentConverter != null)
        {
            if (m_DependentConverter.getField() == null)
                m_DependentConverter.free();    // Special case, must remove converter
            m_DependentConverter = null;
        }
        super.free();
    }
    /**
     * Do I create a separate control for the description for this type of control.
     * Generally yes, but you may want to override in checkboxes or buttons to include the description in the control.
     * @return True as buttons have the description contained in the control.
     */
    public boolean getSeparateFieldDesc()
    {
        if (this.getScreenFieldView() != null)
            return this.getScreenFieldView().getSeparateFieldDesc();
        return false; // **Switch this line to use separate desc (labels) for checkboxes**
    }
    /**
     * Get the button description.
     * @return The button description.
     */
    public String getButtonDesc()
    {
        if (this.getDisplayFieldDesc(this) == false)
            return null;    // Don't display a desc with this control
        if (this.getSeparateFieldDesc())
            return null;    // Field desc is already a separate control, don't display
        if (this.getConverter() != null)
            return this.getConverter().getFieldDesc();  // Or override and supply value
        return null;
    }
    /**
     * Is this control selected?
     * @return True if this button is selected.
     */
    public boolean isSelected()
    {
        return false; // Override for different return
    }
    /**
     * Get the best guess for the command name.
     * This is usually used to get the command to perform for a canned button.
     * @return The button command (or the best guess for the command).
     */
    public String getButtonCommand()
    {
        String strCommand = m_strCommand;
        if (strCommand == null)
            strCommand = this.getButtonDesc();
        if ((strCommand == null) || (strCommand.equals(Constants.BLANK)))
            strCommand = m_strImageButton;  // Use image name until the image is loaded
        return strCommand;
    }
    /**
     * Get the best guess for the command name.
     * This is usually used to get the command to perform for a canned button.
     * @return The button command (or the best guess for the command).
     */
    public void setButtonCommand(String strCommand)
    {
        m_strCommand = strCommand;
    }
    /**
     * Get the best guess for the command name.
     * This is usually used to get the command to perform for a canned button.
     * @return The button image file name.
     */
    public String getImageButtonName()
    {
        return m_strImageButton;
    }
    /**
     * Get the tooltip.
     * @return The tooltip text.
     */
    public String getToolTip()
    {
        return m_strToolTip;
    }
    /**
     * Get the tooltip.
     * @return The tooltip text.
     */
    public void setToolTip(String strToolTip)
    {
        m_strToolTip = strToolTip;
    }
}
