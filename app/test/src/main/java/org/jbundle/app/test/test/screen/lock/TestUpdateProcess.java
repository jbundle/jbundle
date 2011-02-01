/**
 *  @(#)TestUpdateProcess.
 *  Copyright © 2010 tourapp.com. All rights reserved.
 */
package org.jbundle.app.test.test.screen.lock;

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
import org.jbundle.base.thread.*;
import org.jbundle.app.test.test.db.*;

/**
 *  TestUpdateProcess - Update the record..
 */
public class TestUpdateProcess extends BaseProcess
{
    /**
     * Default constructor.
     */
    public TestUpdateProcess()
    {
        super();
    }
    /**
     * Constructor.
     */
    public TestUpdateProcess(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Open the main file.
     */
    public Record openMainRecord()
    {
        return new TestTable(this);
    }
    /**
     * Add the behaviors.
     */
    public void addListeners()
    {
        super.addListeners();
        // Use default lock strategy for now.
        //x this.getMainRecord().setOpenMode((this.getMainRecord().getOpenMode() & ~DBConstants.LOCK_MASK) | DBConstants.OPEN_MERGE_ON_LOCK);
    }
    /**
     * Run Method.
     */
    public void run()
    {
        int iWaitTime;
        
        Record recTestTable = this.getMainRecord();
        try {
            recTestTable.addNew();
            recTestTable.getCounterField().setString("1");
            if (recTestTable.seek(null))
            {
                synchronized(this)
                {
                    try {
                        iWaitTime = (int)(Math.random() * 2.5 * TestScreenLockStorm.WAIT_MULTIPLIER + 1);     // Between 0 and 5 seconds
                        this.wait(iWaitTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                recTestTable.edit();
                ((PropertiesField)recTestTable.getField(TestTable.kTestProperties)).setProperty(this.getProperty("count"), new Date().toString());
                synchronized(this)
                {
                    try {
                        iWaitTime = (int)(Math.random() * 2.5 * TestScreenLockStorm.WAIT_MULTIPLIER + 1);     // Between 0 and 5 seconds
                        this.wait(iWaitTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                recTestTable.set();
            }
            else
                System.out.println("---------------- Error: record not found"); //Never
        } catch (DBException ex) {
            ex.printStackTrace();
        }
    }

}
