/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.report;

/**
 *  HelpHTMLScreen.
 *  Copyright � 1997 jbundle.org. All rights reserved.
 */

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.base.screen.model.util.HelpToolbar;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.model.DBException;
import org.jbundle.model.app.program.db.ClassInfoModel;
import org.jbundle.thin.base.db.Converter;


/**
 *  HTML Help screen.
 *  WARNING: Be very careful; This screen is used differently when it is used for HTML or a Screen.
 *  When it is a screen, it just throws up an HTML Editor window and asks the http server for the
 *  URL that it is suppose to show.
 *  When it is running on the server, it actually formats and builds the html help page.
 */
public class HelpScreen extends BaseParserScreen
{

    /**
     * Constructor.
     */
    public HelpScreen()
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
    public HelpScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     *  Initialize the member fields.
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);

        this.resizeToContent(this.getTitle());
    }
    /**
     * The title for this screen.
     */
    public String getTitle()
    {
        return "Help Screen";
    }
    /**
     * Add the toolbars that belong with this screen.
     */
    public ToolScreen addToolbars()
    {   // Override this to add (call this) or replace (don't call) this default toolbar
        return new HelpToolbar(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, null);
    }
    /**
     * Get the Class Information Service for this screen (use the properties to get the class).
     * If the class information server is not active right now, create it.
     * @exception DBException File exception.
     */
    public ClassInfoModel getClassInfo()
    {
    	ClassInfoModel classInfo = (ClassInfoModel)this.getRecord(ClassInfoModel.CLASS_INFO_FILE);
    	if (classInfo == null)
    		classInfo = (ClassInfoModel)Record.makeRecordFromClassName(ClassInfoModel.THICK_CLASS, this);
        if (classInfo != null)
            classInfo = classInfo.readClassInfo(this, null);
    	return classInfo;
    }
}
