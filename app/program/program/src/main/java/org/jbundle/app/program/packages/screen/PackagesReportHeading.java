/**
 * @(#)PackagesReportHeading.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.packages.screen;

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
import org.jbundle.base.screen.model.report.*;
import org.jbundle.app.program.packages.db.*;
import org.jbundle.app.program.db.*;

/**
 *  PackagesReportHeading - .
 */
public class PackagesReportHeading extends HeadingScreen
{
    /**
     * Default constructor.
     */
    public PackagesReportHeading()
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
     * @param properties Addition properties to pass to the screen.
     */
    public PackagesReportHeading(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
     * Override this to open the other files in the query.
     */
    public void openOtherRecords()
    {
        super.openOtherRecords();
        new JnlpFile(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        this.getScreenRecord().getField(PackagesReportScreenRecord.JNLP_FILE_ID).addListener(new ReadSecondaryHandler(this.getRecord(JnlpFile.JNLP_FILE_FILE)));
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.getRecord(PackagesReportScreenRecord.kPackagesReportScreenRecordFile).getField(PackagesReportScreenRecord.kReportDate).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(PackagesReportScreenRecord.kPackagesReportScreenRecordFile).getField(PackagesReportScreenRecord.kReportTime).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(PackagesReportScreenRecord.kPackagesReportScreenRecordFile).getField(PackagesReportScreenRecord.kReportUserID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        new SEditText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, this.getScreenRecord().getField(PackagesReportScreenRecord.JNLP_FILE_ID), ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(JnlpFile.kJnlpFileFile).getField(JnlpFile.kDescription).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(JnlpFile.kJnlpFileFile).getField(JnlpFile.kShortDesc).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(JnlpFile.kJnlpFileFile).getField(JnlpFile.kTitle).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(JnlpFile.kJnlpFileFile).getField(JnlpFile.kIcon).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }

}
