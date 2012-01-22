/**
 * @(#)TestTableAnalysis.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.test.test.db.analysis;

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
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.screen.model.report.*;
import org.jbundle.app.test.test.db.*;

/**
 *  TestTableAnalysis - .
 */
public class TestTableAnalysis extends AnalysisScreen
{
    /**
     * Default constructor.
     */
    public TestTableAnalysis()
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
    public TestTableAnalysis(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
        return new TestTable(this);
    }
    /**
     * Add the screen fields.
     * Override this to create (and return) the screen record for this recordowner.
     * @return The screen record.
     */
    public Record addScreenRecord()
    {
        return new TestTableSummary(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
    }
    /**
     * Add button(s) to the toolbar.
     */
    public void addToolbarButtons(ToolScreen toolScreen)
    {
        super.addToolbarButtons(toolScreen);
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.getRecord(TestTableSummary.kTestTableSummaryFile).getField(TestTableSummary.kTestCode).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(TestTableSummary.kTestTableSummaryFile).getField(TestTableSummary.kTestKey).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(TestTableSummary.kTestTableSummaryFile).getField(TestTableSummary.kTestCount).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(TestTableSummary.kTestTableSummaryFile).getField(TestTableSummary.kTestShort).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(TestTableSummary.kTestTableSummaryFile).getField(TestTableSummary.kTestDouble).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }
    /**
     * Add/update this summary record.
     * @param recSummary The destination summary record.
     * @param mxKeyFields The key fields map.
     * @param mxDataFields The data fields map.
     */
    public void addSummary(Record recSummary, BaseField[][] mxKeyFields, BaseField[][] mxDataFields)
    {
        this.getRecord(TestTableSummary.kTestTableSummaryFile).getField(TestTableSummary.kTestCode).moveFieldToThis(this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestCode));
        this.getRecord(TestTableSummary.kTestTableSummaryFile).getField(TestTableSummary.kTestKey).moveFieldToThis(this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestKey));
        this.getRecord(TestTableSummary.kTestTableSummaryFile).getField(TestTableSummary.kTestCount).setValue(1);
        this.getRecord(TestTableSummary.kTestTableSummaryFile).getField(TestTableSummary.kTestShort).moveFieldToThis(this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestShort));
        this.getRecord(TestTableSummary.kTestTableSummaryFile).getField(TestTableSummary.kTestDouble).moveFieldToThis(this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestDouble));
        
        super.addSummary(recSummary, mxKeyFields, mxDataFields);
    }

}
