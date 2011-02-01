package org.jbundle.base.screen.model;

/**
* @(#)SButton.java  0.00 12-Feb-97 Don Corley
*
* Copyright (c) 2009 tourapp.com. All Rights Reserved.
*   don@tourgeek.com
*/
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.thin.base.db.Converter;


/**
 * Implements a standard button.
 *
 * @version 1.0.0
 * @author    Don Corley
 *
 * If a converter is associated with this button, data is moved to the field. (controlToField)
 * If NO converter is associated with this button, the button command is sent to the target control. (doCommand).
 */
public class SButtonBox extends SBaseButton
{
    /**
     * The button description.
     */
    protected String m_strButtonDesc = null;

    /**
     * Constructor.
     */
    public SButtonBox()
    {
        super();
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public SButtonBox(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        this();
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, null, null, null, null, null);
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
    public SButtonBox(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, String strValue, String strDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, strValue, strDesc, null, null, null);
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param strValue The value to set the field on button press.
     * @param strImage The image filename for this button.
     */
    public SButtonBox(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, String strImage, int iDisplayFieldDesc, String strValue)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, strValue, null, strImage, null, null);
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
     */
    public SButtonBox(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, String strValue, String strDesc, String strImage, String strCommand, String strToolTip)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, strValue, strDesc, strImage, strCommand, strToolTip);
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
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, String strValue, String strDesc, String strImage, String strCommand, String strToolTip)
    {
        if (DBConstants.BLANK.equals(strDesc))
            strDesc = null;     // Since resources return Blank instead of null.
        m_strButtonDesc = strDesc;
        m_strCommand = strCommand;
        super.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, strValue, null, strImage, null, strToolTip);

        this.setRequestFocusEnabled(false);     // By default, make user click with mouse
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get the button description.
     * @return The button description.
     */
    public String getButtonDesc()
    {
        if ((m_strButtonDesc != null) && (m_strButtonDesc.length() > 0))
            return m_strButtonDesc;
        else
            return super.getButtonDesc();
    }
    /**
     * Set this control's converter to this HTML param.
     * ie., Check to see if this button was pressed.
     */
    public int setSFieldValue(String strParamValue, boolean bDisplayOption, int iMoveMode)
    {
        String strButtonDesc = this.getButtonDesc();
        String strButtonCommand = this.getButtonCommand();
        if (strButtonCommand != null)
            if (strButtonDesc != null)
                if (strButtonDesc.equals(strParamValue))
        {   // Button was pressed, do command
            this.handleCommand(strButtonCommand, this, ScreenConstants.USE_NEW_WINDOW);
            return DBConstants.NORMAL_RETURN;
        }
        // Often this is called in a report needing a value set, so set it:
        return super.setSFieldValue(strParamValue, bDisplayOption, iMoveMode);
    }
    /**
     * Is this a valid Html Input field?
     * @return true if this is an HTML Input field.
     */
    public boolean isInputField()
    {
        return super.isInputField();	// Must be enabled, so the control will display for HTML
    }
}
