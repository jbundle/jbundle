/**
 * @(#)TestReptileXmlReport.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.test.vet.shared.xml;

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
import org.jbundle.app.test.vet.shared.db.*;
import org.jbundle.app.test.vet.db.*;
import org.jbundle.app.test.vet.screen.*;

/**
 *  TestReptileXmlReport - .
 */
public class TestReptileXmlReport extends ReportScreen
{
    /**
     * Default constructor.
     */
    public TestReptileXmlReport()
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
    public TestReptileXmlReport(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
        return new Reptile(this);
    }
    /**
     * Override this to open the other files in the query.
     */
    public void openOtherRecords()
    {
        super.openOtherRecords();
    }
    /**
     * Add the screen fields.
     * Override this to create (and return) the screen record for this recordowner.
     * @return The screen record.
     */
    public Record addScreenRecord()
    {
        return new TestReportScreenRecord(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        
        Record recVet = ((ReferenceField)this.getMainRecord().getField(Reptile.VET_ID)).getReferenceRecord();
        this.getMainRecord().getField(Reptile.VET_ID).addListener(new ReadSecondaryHandler(recVet));
    }
    /**
     * Add the toolbars that belong with this screen.
     * @return The new toolbar.
     */
    public ToolScreen addToolbars()
    {
        return new TestReptileXmlToolbar(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, null);
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.addColumn(new MultipleTableFieldConverter(this.getRecord(Reptile.REPTILE_FILE), Reptile.NAME));
        this.addColumn(new MultipleTableFieldConverter(this.getRecord(Reptile.REPTILE_FILE), Reptile.WEIGHT));
        this.addColumn(new MultipleTableFieldConverter(this.getRecord(Reptile.REPTILE_FILE), Reptile.VET_ID, Vet.NAME));
        //?new TestReptileXmlRecordDetail(null, null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC | HtmlConstants.FOOTING_SCREEN);
    }
    /**
     * Get the path to the target servlet.
     * @param The servlet type (regular html or xhtml)
     * @return the servlet path.
     */
    public String getServletPath(String strServletParam)
    {
        return super.getServletPath(DBParams.XHTMLSERVLET); // Use cocoon
    }

}
