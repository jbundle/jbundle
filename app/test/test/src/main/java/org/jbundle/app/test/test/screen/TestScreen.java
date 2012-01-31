/**
 * @(#)TestScreen.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.test.test.screen;

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

/**
 *  TestScreen - .
 */
public class TestScreen extends Screen
{
    /**
     * Default constructor.
     */
    public TestScreen()
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
    public TestScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
        this.addMainKeyBehavior();
        this.getRecord(TestTable.TEST_TABLE_FILE).getField(TestTable.TEST_DOUBLE).addListener(new CheckRangeHandler(5, 10));
        this.getRecord(TestTable.TEST_TABLE_FILE).getField(TestTable.TEST_NAME).addListener(new FieldToUpperHandler(null));
        this.getRecord(TestTable.TEST_TABLE_FILE).getField(TestTable.TEST_KEY).addListener(new RegisterValueHandler(null));
        
        this.getRecord(TestTable.TEST_TABLE_FILE).getField(TestTable.TEST_CODE).addListener(new ChangeFocusOnChangeHandler(this.getRecord(TestTable.TEST_TABLE_FILE).getField(TestTable.TEST_LONG)));
    }
    /**
     * Set up all the screen fields.
     */
    public void setupSFields()
    {
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kID).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestCode).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestName).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestYesNo).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestLong).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestShort).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestDateTime).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestDate).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestTime).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestFloat).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
                new SPopupBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestYesNo), ScreenConstants.DEFAULT_DISPLAY);
        
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestDouble).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestPercent).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestSecond).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
                new SCannedBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, "Next");
        
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestReal).setupDefaultView(this.getNextLocation(ScreenConstants.TOP_NEXT, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestCurrency).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestDouble).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestKey).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        
                new SEditText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestYesNo), ScreenConstants.DEFAULT_DISPLAY);
                new SButtonBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestYesNo), ScreenConstants.DEFAULT_DISPLAY);
                new SToggleButton(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestYesNo), ScreenConstants.DEFAULT_DISPLAY);
                new SCheckBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestYesNo), ScreenConstants.DEFAULT_DISPLAY);
        
        //      new SPopupBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestKey), ScreenConstants.DEFAULT_DISPLAY);
        
                new SRadioButton(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestYesNo), ScreenConstants.DEFAULT_DISPLAY);
                new SStaticString(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, "Static String");
                new SStaticText(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestYesNo), ScreenConstants.DEFAULT_DISPLAY);
        
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestMemo).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestHtml).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestXml).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
                this.getRecord(TestTable.kTestTableFile).getField(TestTable.kTestImage).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }

}
