/**
 * @(#)PackagesReportDetail.
 * Copyright © 2012 jbundle.org. All rights reserved.
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
 *  PackagesReportDetail - .
 */
public class PackagesReportDetail extends ReportScreen
{
    /**
     * Default constructor.
     */
    public PackagesReportDetail()
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
    public PackagesReportDetail(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
     * Get the main record for this screen.
     * @return The main record.
     */
    public Record getMainRecord()
    {
        return this.getRecord(Packages.PACKAGES_FILE);
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.getRecord(Packages.PACKAGES_FILE).getField(Packages.NAME).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        Converter converter = this.getRecord(Packages.PACKAGES_FILE).getField(Packages.PARENT_FOLDER_ID);
        new SEditText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, converter, ScreenConstants.DEFAULT_DISPLAY);
        converter = this.getRecord(Packages.PACKAGES_FILE).getField(Packages.CLASS_PROJECT_ID);
        new SEditText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, converter, ScreenConstants.DEFAULT_DISPLAY);
        Record packages = this.getMainRecord();
        StringField field = new StringField(packages, "Path", DBConstants.DEFAULT_FIELD_LENGTH, "Path", null);
        field.setVirtual(true);     // Being careful
        packages.addField(field);
        converter = new FieldConverter(field)
        {
            public String getString() 
            {
                Record packages = ((BaseField)this.getField()).getRecord();
                ClassProject classProject = (ClassProject)((ReferenceField)packages.getField(Packages.CLASS_PROJECT_ID)).getReference();
                ClassProject.CodeType codeType = ((CodeTypeField)packages.getRecord(Packages.PACKAGES_FILE).getField(Packages.CODE_TYPE)).getCodeType();
                String path = DBConstants.BLANK;
                if (classProject != null)
                    if ((classProject.getEditMode() == DBConstants.EDIT_IN_PROGRESS) || (classProject.getEditMode() == DBConstants.EDIT_CURRENT))
                    path = classProject.getFileName(null, null, codeType, false, false);
                return path;
            }
        };
        new SEditText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, converter, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Packages.PACKAGES_FILE).getField(Packages.COMMENT).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Packages.PACKAGES_FILE).getField(Packages.CODE).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Packages.PACKAGES_FILE).getField(Packages.RECURSIVE).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(PackagesReportScreenRecord.PACKAGES_REPORT_SCREEN_RECORD_FILE).getField(PackagesReportScreenRecord.PACKAGES_TREE).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(PackagesReportScreenRecord.PACKAGES_REPORT_SCREEN_RECORD_FILE).getField(PackagesReportScreenRecord.EXCLUDE_PACKAGES).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }

}
