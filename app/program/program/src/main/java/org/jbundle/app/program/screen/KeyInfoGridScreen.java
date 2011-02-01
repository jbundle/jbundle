/**
 *  @(#)KeyInfoGridScreen.
 *  Copyright © 2010 tourapp.com. All rights reserved.
 */
package org.jbundle.app.program.screen;

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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.app.program.db.*;

/**
 *  KeyInfoGridScreen - .
 */
public class KeyInfoGridScreen extends GridScreen
{
    /**
     * Default constructor.
     */
    public KeyInfoGridScreen()
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
    public KeyInfoGridScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
        return new KeyInfo(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        Record record = this.getMainRecord();
        Record recClassInfo = this.getRecord(ClassInfo.kClassInfoFile);
        if (recClassInfo != null)
        {
            record.setKeyArea(KeyInfo.kKeyFilenameKey);
            SubFileFilter listener = new SubFileFilter(recClassInfo.getField(ClassInfo.kClassName), KeyInfo.kKeyFilename, null, -1, null, -1, true);
            record.addListener(listener);
            recClassInfo.getField(ClassInfo.kClassName).addListener(new FieldReSelectHandler(this));
        }
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.getRecord(KeyInfo.kKeyInfoFile).getField(KeyInfo.kKeyName).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(KeyInfo.kKeyInfoFile).getField(KeyInfo.kKeyType).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(KeyInfo.kKeyInfoFile).getField(KeyInfo.kKeyField1).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }

}
