/**
 * @(#)TestControlGridScreen.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.test.test.screen.control;

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
import org.jbundle.app.test.test.db.*;
import org.jbundle.app.test.test.screen.*;

/**
 *  TestControlGridScreen - .
 */
public class TestControlGridScreen extends GridScreen
{
    /**
     * Default constructor.
     */
    public TestControlGridScreen()
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
    public TestControlGridScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        this.getScreenRecord().getField(TestScreenRecord.SORT_KEY).addListener(new SortOrderHandler(this));        
        
        BaseField fldTargetValue = this.getMainRecord().getField(TestTable.TEST_FLOAT);
        BaseField fldValue1 = this.getMainRecord().getField(TestTable.TEST_LONG);
        BaseField fldValue2 = this.getMainRecord().getField(TestTable.TEST_SHORT);
        this.getMainRecord().getField(TestTable.TEST_SHORT).addListener(new CalcBalanceHandler(fldTargetValue, fldValue1, fldValue2, CalcBalanceHandler.MULTIPLY, false));
    }
    /**
     * Add the navigation button(s) to the left of the grid row.
     */
    public void addNavButtons()
    {
        new SCannedBox(this.getNextLocation(ScreenConstants.FIRST_SCREEN_LOCATION, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, null, null, MenuConstants.DELETE, MenuConstants.DELETE, null);
        super.addNavButtons();
    }
    /**
     * Add the screen fields.
     * Override this to create (and return) the screen record for this recordowner.
     * @return The screen record.
     */
    public Record addScreenRecord()
    {
        return new TestScreenRecord(this);
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestCode).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestName).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestLong).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestShort).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestFloat).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);

        new SEditText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestYesNo), ScreenConstants.DEFAULT_DISPLAY);
        new SButtonBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestYesNo), ScreenConstants.DEFAULT_DISPLAY);
        new SToggleButton(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestYesNo), ScreenConstants.DEFAULT_DISPLAY);
        new SCheckBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestYesNo), ScreenConstants.DEFAULT_DISPLAY);
        new SRadioButton(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestYesNo), ScreenConstants.DEFAULT_DISPLAY);
        new SStaticText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestYesNo), ScreenConstants.DEFAULT_DISPLAY);
        
        this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestFloat).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }

}
