/*

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.report;

/**
* @(#)Screen.java 0.00 12-Feb-97 Don Corley
*
* Copyright (c) 2009 tourapp.com. All Rights Reserved.
*   don@tourgeek.com
*/
import java.io.PrintWriter;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.BaseScreen;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Converter;

/**
 * This is the base for any data maintenance screen.
 */
public class HeadingScreen extends BaseScreen
{
    /**
     * Constructor.
     */
    public HeadingScreen()
    {
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
    public HeadingScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * None.
     */
    public ToolScreen addToolbars()
    {
        return null;
    }
    /**
     * Output this screen using HTML.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iPrintOptions)
    {
        super.printData(out, iPrintOptions);
        return false;
    }
}
