/**
 * @(#)AnimalGridScreen.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.test.vet.screen;

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
import org.jbundle.app.test.vet.db.*;

/**
 *  AnimalGridScreen - .
 */
public class AnimalGridScreen extends GridScreen
{
    /**
     * Default constructor.
     */
    public AnimalGridScreen()
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
    public AnimalGridScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
        return new Animal(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        this.getMainRecord().setKeyArea(Animal.kNameKey);
        this.setEditing(true);
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        Record record = this.getMainRecord();
        this.addColumn(new MultipleTableFieldConverter(record, Animal.kName));
    }

}
