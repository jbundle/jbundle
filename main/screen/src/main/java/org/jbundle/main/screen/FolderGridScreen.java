/**
 * @(#)FolderGridScreen.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.screen;

import java.awt.*;
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
import org.jbundle.main.db.*;

/**
 *  FolderGridScreen - .
 */
public class FolderGridScreen extends BaseFolderGridScreen
{
    /**
     * Default constructor.
     */
    public FolderGridScreen()
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
    public FolderGridScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
     * FolderGridScreen Method.
     */
    public FolderGridScreen(Record recHeader, Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object>
     properties)
    {
        this();
        this.init(recHeader, record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        return new Folder(this);
    }
    /**
     * OpenHeaderRecord Method.
     */
    public Record openHeaderRecord()
    {
        super.openHeaderRecord();
        if (m_recHeader == null)
        {
            Record record = this.getMainRecord();
            try   {
                m_recHeader = (Record)record.clone(); // Do not add to screen's list - will mix with other file
            } catch (CloneNotSupportedException ex)   {
                ex.printStackTrace();
            }
            // Do not clone the listeners,
            while (m_recHeader.getListener() != null)
            {
                m_recHeader.removeListener(m_recHeader.getListener(), true);
            }
            m_recHeader.addListeners();   // Just use the standard listeners
        }
        return m_recHeader;
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        // This beginning code is a trick to make the grid screen think that a zero record is the header.
        this.setProperty("fakeHeader", DBConstants.FALSE);
        if (m_recHeader != null)
            if ((m_recHeader.getEditMode() == DBConstants.EDIT_ADD) || (m_recHeader.getEditMode() == DBConstants.EDIT_NONE))
                if (this.getProperty(DBParams.HEADER_OBJECT_ID) == null)
                {
                    this.setProperty("fakeHeader", DBConstants.TRUE);
                    this.setProperty(DBParams.HEADER_OBJECT_ID, "0");
                    if (m_recHeader.getCounterField() != null)
                        if (m_recHeader.getCounterField().getDefault() == null)
                    {
                        m_recHeader.getCounterField().setDefault(new Integer(0)); // This will guarantee that the sub record's parent field is non-null
                        try {
                            m_recHeader.addNew();
                        } catch (DBException ex) {
                        }
                    }
                }
        super.addListeners();
    }
    /**
     * AddSubFileFilter Method.
     */
    public void addSubFileFilter()
    {
        // Override this if it is not correct.
        SubFileFilter listener = null;
        this.getMainRecord().setKeyArea(BaseFolder.PARENT_FOLDER_ID_KEY);
        this.getMainRecord().addListener(listener = new SubFileFilter(this.getHeaderRecord()));
        if (this.getMainRecord().getKeyArea().getField(DBConstants.MAIN_KEY_FIELD).isNullable() == false)
        {
            listener.setFilterIfNull(true);
            listener.setAddNewHeaderOnAdd(false);
        }
        // Set it back
        if (DBConstants.TRUE.equalsIgnoreCase(this.getProperty("fakeHeader")))
            this.setProperty(DBParams.HEADER_OBJECT_ID, DBConstants.BLANK);
    }
    /**
     * Make a sub-screen.
     * @return the new sub-screen.
     */
    public BasePanel makeSubScreen()
    {
        return new FolderHeaderScreen(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, null);
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.getRecord(Folder.FOLDER_FILE).getField(Folder.NAME).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }

}
