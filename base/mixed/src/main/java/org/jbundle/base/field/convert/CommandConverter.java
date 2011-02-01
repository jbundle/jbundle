package org.jbundle.base.field.convert;

/**
 * @(#)CheckConverter.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.SButtonBox;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.db.Converter;


/**
 * This is a special converter that when connected to a command button
 * calls doCommand when the button is pressed.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class CommandConverter extends FieldConverter
{
    /**
     * Constructor.
     */
    public CommandConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The target converter to set to this string.
     * @param strTargetValue The string to set the converter to is set to true.
     * @param strAltFieldDesc An alternate description to display.
     * @param bTrueIfMatch If true, set to the alternate value.
     */
    public CommandConverter(Converter converter)
    {
        this();
        this.init(converter);
    }
    /**
     * Init this converter.
     * @param converter The target converter to set to this string.
     * @param strTargetValue The string to set the converter to is set to true.
     * @param fldTargetValue The string to set this field if set to true.
     * @param strAltFieldDesc An alternate description to display.
     * @param bTrueIfMatch If true, set to the alternate value.
     * @param boolMaskValue
     */
    public void init(Converter converter)
    {
        super.init(converter);
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Set the state of this field for binary fields (don't override this).
     * Usually overidden.
     * @param bState the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setState(boolean bState, boolean bDisplayOption, int iMoveMode)
    {
        return this.doCommand(bDisplayOption, iMoveMode);
    }
    /**
     * The button for this converter was pressed do the command.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int doCommand(boolean bDisplayOption, int iMoveMode)
    {
        return DBConstants.NORMAL_RETURN;   // Override this to process this command.
    }
    /**
     * Set up the default control for this field (A SCheckBox).
     *  @param  itsLocation     Location of this component on screen (ie., GridBagConstraint).
     *  @param  targetScreen    Where to place this component (ie., Parent screen or GridBagLayout).
     *  @param  iDisplayFieldDesc Display the label? (optional).
     *  @return   Return the component or ScreenField that is created for this field.
     */
    public ScreenField setupDefaultView(ScreenLocation itsLocation, BasePanel targetScreen, Converter converter, int iDisplayFieldDesc)   // Add this view to the list
    {
        return new SButtonBox(itsLocation, targetScreen, converter, iDisplayFieldDesc);
    }
}
