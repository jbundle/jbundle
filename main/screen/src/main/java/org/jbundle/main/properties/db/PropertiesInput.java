/**
 * @(#)PropertiesInput.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.properties.db;

import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import java.util.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.model.main.properties.db.*;

/**
 *  PropertiesInput - Memory table for inputting properties.
 */
public class PropertiesInput extends Record
     implements PropertiesInputModel
{
    private static final long serialVersionUID = 1L;

    protected PropertiesField m_fldProperties = null;
    /**
     * Default constructor.
     */
    public PropertiesInput()
    {
        super();
    }
    /**
     * Constructor.
     */
    public PropertiesInput(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwner screen)
    {
        m_fldProperties = null;
        super.init(screen);
    }
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(PROPERTIES_INPUT_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Properties";
    }
    /**
     * Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "main";
    }
    /**
     * Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return DBConstants.UNSHAREABLE_MEMORY;
    }
    /**
     * Make a default screen.
     */
    public ScreenParent makeScreen(ScreenLoc itsLocation, ComponentParent parentScreen, int iDocMode, Map<String,Object> properties)
    {
        ScreenParent screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = Record.makeNewScreen(PROPERTIES_INPUT_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) == ScreenConstants.DISPLAY_MODE)
            screen = Record.makeNewScreen(PROPERTIES_INPUT_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else
            screen = super.makeScreen(itsLocation, parentScreen, iDocMode, properties);
        return screen;
    }
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == 0)
            field = new CounterField(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 1)
            field = new StringField(this, KEY, 128, null, null);
        if (iFieldSeq == 2)
            field = new StringField(this, VALUE, 255, null, null);
        if (iFieldSeq == 3)
            field = new StringField(this, COMMENT, 255, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }
    /**
     * Add this key area description to the Record.
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == 0)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, ID_KEY);
            keyArea.addKeyField(ID, DBConstants.ASCENDING);
        }
        if (iKeyArea == 1)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, KEY_KEY);
            keyArea.addKeyField(KEY, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
        return keyArea;
    }
    /**
     * SetPropertiesField Method.
     */
    public void setPropertiesField(Field fldProperties)
    {
        if (fldProperties != null)
        {
            m_fldProperties = (PropertiesField)fldProperties;
            this.loadFieldProperties();
            this.addListener(new FileListener(null)
            {
                public void setOwner(ListenerOwner owner)
                {
                    if (owner == null)
                        if (this.getOwner() != null)
                            ((PropertiesInput)PropertiesInput.this).restoreFieldProperties();
                    super.setOwner(owner);
                }
            });
        }
    }
    /**
     * Load the field properties from the field and add them to this record.
     */
    public void loadFieldProperties()
    {
        this.loadFieldProperties(m_fldProperties);
    }
    /**
     * LoadFieldProperties Method.
     */
    public void loadFieldProperties(Field fldProperties)
    {
        if (fldProperties == null)
            return;
        boolean[] rgbEnabled = this.setEnableListeners(false);;
        try {
            this.setKeyArea(PropertiesInput.KEY_KEY);
            // First, delete the old records
            this.close();
            while (this.hasNext())
            {
                this.next();
                this.edit();
                this.remove();
            }
            // Now, add the properties to the record
            Map<String,Object> properties = ((PropertiesField)fldProperties).getProperties();
            Iterator<String> iterator = properties.keySet().iterator();
            while (iterator.hasNext())
            {
                String strKey = iterator.next();
                String strValue = (String)properties.get(strKey);
                this.addNew();
                this.getField(PropertiesInput.KEY).setString(strKey);
                if (this.seek(null))
                {
                    this.edit();
                    this.remove();
                }
                this.addNew();
                this.getField(PropertiesInput.KEY).setString(strKey);
                this.getField(PropertiesInput.VALUE).setString(strValue);
                this.add();
            }
            if (this.getRecordOwner() instanceof GridScreen)
                ((GridScreen)this.getRecordOwner()).reSelectRecords();
        } catch (DBException ex) {
            ex.printStackTrace();
        } finally {
            this.setEnableListeners(rgbEnabled);
        }
    }
    /**
     * RestoreFieldProperties Method.
     */
    public void restoreFieldProperties()
    {
        this.restoreFieldProperties(m_fldProperties);
    }
    /**
     * RestoreFieldProperties Method.
     */
    public void restoreFieldProperties(PropertiesField fldProperties)
    {
        if (fldProperties == null)
            return;
        try {
            // This may seem a little wierd, but by doing this, I won't change the field if there was no change.
            Map<String,Object> properties = fldProperties.getProperties();
            this.close();
            while (this.hasNext())
            {
                this.next();
                String strKey = this.getField(PropertiesInput.KEY).getString();
                String strValue = this.getField(PropertiesInput.VALUE).getString();
                if (strValue != null)
                    if (strValue.length() > 0)
                {
                    fldProperties.setProperty(strKey, strValue);
                    properties.remove(strKey);
                }
            }
            Iterator<String> iterator = properties.keySet().iterator();
            while (iterator.hasNext())
            {
                String strKey = iterator.next();
                fldProperties.setProperty(strKey, null);
            }
        } catch (DBException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * StartEditor Method.
     */
    public ScreenComponent startEditor(Field fldProperties, boolean bAllowAppending, Map<String,Object> mapKeyDescriptions)
    {
        this.setPropertiesField(fldProperties);
        BaseApplication application = ((BaseApplication)((PropertiesField)fldProperties).getRecord().getRecordOwner().getTask().getApplication());
        BasePanel screenParent = Screen.makeWindow(application);
        GridScreen screen = (GridScreen)this.makeScreen(null, screenParent, ScreenConstants.DISPLAY_MODE, mapKeyDescriptions);
        screen.setAppending(bAllowAppending);
        
        ((PropertiesField)fldProperties).getRecord().addDependentScreen(screen);
        
        return screen;
    }

}
