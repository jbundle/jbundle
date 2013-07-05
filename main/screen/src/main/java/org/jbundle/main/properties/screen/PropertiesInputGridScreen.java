/**
 * @(#)PropertiesInputGridScreen.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.properties.screen;

import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.main.properties.db.*;

/**
 *  PropertiesInputGridScreen - .
 */
public class PropertiesInputGridScreen extends GridScreen
{
    protected Properties m_properties = new Properties();
    /**
     * Default constructor.
     */
    public PropertiesInputGridScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?.
     */
    public PropertiesInputGridScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        return new PropertiesInput(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        if (m_properties.size() > 0)
        {
            Record recPropertiesInput = this.getMainRecord();
            recPropertiesInput.setKeyArea(PropertiesInput.KEY_KEY);
            Iterator<Object> iterator = m_properties.keySet().iterator();
            while (iterator.hasNext())
            {
                String strKey = (String)iterator.next();
                try {
                    recPropertiesInput.addNew();
                    recPropertiesInput.getField(PropertiesInput.KEY).setString(strKey);
                    if (!recPropertiesInput.seek(null))
                    {
                        recPropertiesInput.addNew();
                        recPropertiesInput.getField(PropertiesInput.KEY).setString(strKey);
                        recPropertiesInput.add();
                    }
                } catch (DBException ex) {
                    ex.printStackTrace();
                }
            }
            //?this.getMainRecord().getField(PropertiesInput.KEY).setEnabled(false);
        }
    }
    /**
     * Add the toolbars that belong with this screen.
     * @return The new toolbar.
     */
    public ToolScreen addToolbars()
    {
        if (this.getParentScreen() instanceof Screen)
            return null;    // Sub-screen
        return super.addToolbars();
    }
    /**
     * Does the current user have permission to access this screen.
     * @return NORMAL_RETURN if access is allowed, ACCESS_DENIED or LOGIN_REQUIRED otherwise.
     */
    public int checkSecurity()
    {
        return DBConstants.NORMAL_RETURN; // Since this is always a screen control, no security is necessary
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        Converter converter = this.getRecord(PropertiesInput.PROPERTIES_INPUT_FILE).getField(PropertiesInput.KEY);
        converter = new FieldLengthConverter(converter, 20);
        if (m_properties.size() > 0)
        {
            converter = new FieldConverter(converter)
            {
                /**
                 * Get the data on the end of this converter chain.
                 * @return The raw data.
                 */
                public Object getData() 
                {
                    Object data = super.getData();
                    if (data instanceof String)
                        if (m_properties.getProperty((String)data) != null)
                            data = m_properties.getProperty((String)data);
                    return data;
                }
                /**
                 * Retrieve (in string format) from this field.
                 * @return The data in string format.
                 */
                public String getString()
                {
                    String data = super.getString();
                    if (m_properties.getProperty((String)data) != null)
                        data = m_properties.getProperty(data);
                    return data;
                }
            };
        }
        new SEditText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, converter, ScreenConstants.DEFAULT_DISPLAY);
        converter = this.getRecord(PropertiesInput.PROPERTIES_INPUT_FILE).getField(PropertiesInput.VALUE);
        converter = new FieldLengthConverter(converter, 30);
        new SEditText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, converter, ScreenConstants.DEFAULT_DISPLAY);
    }
    /**
     * Add the navigation button(s) to the left of the grid row.
     */
    public void addNavButtons()
    {
        if ((m_iDisplayFieldDesc & ScreenConstants.SELECT_MODE) != ScreenConstants.SELECT_MODE)
            new SCannedBox(this.getNextLocation(ScreenConstants.FIRST_SCREEN_LOCATION, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, null, null, MenuConstants.DELETE, MenuConstants.DELETE, null);
        if (!(this.getParentScreen() instanceof Screen))
            super.addNavButtons();  // Only allow form screen if not sub-window
    }
    /**
     * Process the command.
     * <br />Step 1 - Process the command if possible and return true if processed.
     * <br />Step 2 - If I can't process, pass to all children (with me as the source).
     * <br />Step 3 - If children didn't process, pass to parent (with me as the source).
     * <br />Note: Never pass to a parent or child that matches the source (to avoid an endless loop).
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param iCommandOptions If this command creates a new screen, create in a new window?
     * @return true if success.
     */
    public boolean doCommand(String strCommand, ScreenField sourceSField, int iCommandOptions)
    {
        if ((MenuConstants.FORMLINK.equalsIgnoreCase(strCommand)) || (MenuConstants.FORM.equalsIgnoreCase(strCommand)))
            return true;    // Ignore these commands
        return super.doCommand(strCommand, sourceSField, iCommandOptions);
    }
    /**
     * Set this property.
     * @param strProperty The property key.
     * @param strValue The property value.
     */
    public void setProperty(String strProperty, String strValue)
    {
        m_properties.setProperty(strProperty, strValue);
    }
    /**
     * ClearStatusText Method.
     */
    public void clearStatusText()
    {
        // Don't call super - Don't let property screen changes clear the status message.
    }

}
