/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model;

/**
 * @(#)STreeCtrl.java 0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.model.db.Convert;
import org.jbundle.thin.base.db.Converter;


/**
 * Implements a tree control.
 */
public class STreeCtrl extends BaseScreen
{

    /**
     * Constructor.
     */
    public STreeCtrl()  {
        super();
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public STreeCtrl(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Open the files and setup the screen.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(Record mainRecord, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(mainRecord, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);

        this.resizeToContent(this.getTitle());
    }
    /**
     * Set up the screen fields (default = set them all up for the current record).
     * @param converter The converter to set up the next default control for.
     */
    public Object addColumn(Converter converter)
    {
        return converter.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, ScreenConstants.DEFAULT_DISPLAY);
    }
    /**
     * The title for this screen.
     * @return The title.
     */
    public String getTitle()    // Standard file maint for this record (returns new record)
    { // This is almost always overidden!
        String windowName = "Tree"; // Default
        Record query = this.getMainRecord();
        if (query != null)
            windowName = query.getRecordName() + ' ' + windowName;
        return windowName;
    }
    /**
     *  Get the converter for this column.
     *  This method is only used by FlexTreeHandler!
     * @param iSelectIndex The field to return.
     * @return The converter at this location.
     */
    public Convert getTreeField(int iSelectIndex)
    {
        return this.getSField(iSelectIndex).getConverter();
    }
}
