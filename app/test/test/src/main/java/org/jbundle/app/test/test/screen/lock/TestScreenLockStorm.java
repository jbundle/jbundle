/**
 * @(#)TestScreenLockStorm.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.app.test.test.screen.*;
import org.jbundle.thin.base.screen.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.base.message.trx.message.*;
import org.jbundle.main.msg.db.*;
import org.jbundle.base.thread.*;
import javax.swing.*;
import org.jbundle.app.test.test.db.*;

/**
 *  TestScreenLockStorm - .
 */
public class TestScreenLockStorm extends TestScreen
{
    public static final int WAIT_MULTIPLIER = 100;
    public static final int COUNT = 10;
    /**
     * Default constructor.
     */
    public TestScreenLockStorm()
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
    public TestScreenLockStorm(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
     * Add the screen fields.
     * Override this to create (and return) the screen record for this recordowner.
     * @return The screen record.
     */
    public Record addScreenRecord()
    {
        return new TourEventScreenRecord(this);
    }
    /**
     * Run Method.
     */
    public void run()
    {
        super.run();
        //? this.getMainRecord().setOpenMode(this.getMainRecord().getOpenMode() | DBConstants.OPEN_LOCK_ON_EDIT);
    }
    /**
     * Set up all the screen fields.
     */
    public void setupSFields()
    {
        super.setupSFields();
        this.getRecord(TestTable.TEST_TABLE_FILE).getField(TestTable.TEST_PROPERTIES).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        new SCannedBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, "Start storm");
        new SCannedBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, "Stop storm"); 
        new SCannedBox(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.SET_ANCHOR), this, null, ScreenConstants.DEFAULT_DISPLAY, "Auto storm"); 
        //this.getScreenRecord().getField(TourEventScreenRecord.RUN_PROCESS_IN).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }
    /**
     * Process the command.
     * <br />Step 1 - Process the command if possible and return true if processed.
     * <br />Step 2 - If I can't process, pass to all children (with me as the source).
     * <br />Step 3 - If children didn't process, pass to parent (with me as the source).
     * <br />Note: Never pass to a parent or child that matches the source (to avoid an endless loop).
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param iCommandOptions If this command creates a new screen, create in a new window?
     * @return true if success.
     */
    public boolean doCommand(String strCommand, ScreenField sourceSField, int iCommandOptions)
    {
        if ("Start storm".equalsIgnoreCase(strCommand))
            return this.startStorm();
        if ("Stop storm".equalsIgnoreCase(strCommand))
            return this.stopStorm();
        if ("Auto storm".equalsIgnoreCase(strCommand))
            return this.autoStorm();
        return super.doCommand(strCommand, sourceSField, iCommandOptions);
    }
    /**
     * StartStorm Method.
     */
    public boolean startStorm()
    {
        Map<String,Object> properties = new HashMap<String,Object>();
        properties.put(DBParams.PROCESS, MessageStormProcess.class.getName());
        Application app = (Application)this.getTask().getApplication();
        String strQueueName = MessageConstants.TRX_SEND_QUEUE;
        String strQueueType = MessageConstants.INTRANET_QUEUE;
        BaseMessage message = new MapMessage(new TrxMessageHeader(strQueueName, strQueueType, properties), properties);
        String strProcess = Utility.propertiesToURL(null, properties);
        
        //if (RunProcessInField.REMOTE_PROCESS.equalsIgnoreCase(this.getScreenRecord().getField(TourEventScreenRecord.RUN_PROCESS_IN).toString()))
        {
            app.getMessageManager().sendMessage(message);
        }
        //else if (RunProcessInField.LOCAL_PROCESS.equalsIgnoreCase(this.getScreenRecord().getField(TourEventScreenRecord.RUN_PROCESS_IN).toString()))
        {
            app.getTaskScheduler().addTask(new ProcessRunnerTask(app, strProcess, null));
        }
       // else // LOCAL
        {
            new ProcessRunnerTask(app, strProcess, null).run();
        }
        
        return true;
    }
    /**
     * StopStorm Method.
     */
    public boolean stopStorm()
    {
        return true;
    }
    /**
     * AutoStorm Method.
     */
    public boolean autoStorm()
    {
        Record record = this.getMainRecord();
        try {
            record.addNew();
            record.getCounterField().setString("1");
            record.setKeyArea(DBConstants.MAIN_KEY_AREA);
            if (record.seek(null))
            {
                record.edit();
                record.getField(TestTable.TEST_PROPERTIES).setData(null);
                record.set();
                
                record.seek(null);
                
                this.startStorm();
        
                SwingWorker<String,Object> worker = new SwingWorker<String,Object>()
                {
                    
                    protected String doInBackground() throws Exception
                    {
                        synchronized(Thread.currentThread())
                        {
                            try {
                                Thread.currentThread().wait(25 * WAIT_MULTIPLIER); // 25 Seconds should be plenty
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                    /**
                     * Called on the event dispatching thread (not on the worker thread)
                     * after the <code>construct</code> method has returned.
                     */
                    public void done()
                    {
                        Record record = getMainRecord();
                        for (int i = 1; i <=COUNT; i++)
                        {
                            if (((PropertiesField)record.getField(TestTable.TEST_PROPERTIES)).getProperty(Integer.toString(i)) == null)
                                return;
                        }
                     
                        autoStorm();
                    }
                };
                worker.execute();
            }
        } catch (DBException e) {
            e.printStackTrace();
        }        
        
        return true;  
    }

}
