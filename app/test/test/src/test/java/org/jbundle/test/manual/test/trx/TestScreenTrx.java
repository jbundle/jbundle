/*
 *  @(#)TestScreenLockStorm.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.trx;

import java.util.HashMap;
import java.util.Map;

import org.jbundle.app.test.test.db.TestTable;
import org.jbundle.app.test.test.screen.TestScreen;
import org.jbundle.app.test.test.screen.lock.TourEventScreenRecord;
import org.jbundle.base.db.Record;
import org.jbundle.base.message.trx.message.TrxMessageHeader;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.SCannedBox;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.main.msg.db.RunProcessInField;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.MapMessage;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.util.Application;


/**
 *  TestScreenLockStorm - .
 */
public class TestScreenTrx extends TestScreen
{
    /**
     * Default constructor.
     */
    public TestScreenTrx()
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
    public TestScreenTrx(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
        this.getScreenRecord().getField(TourEventScreenRecord.RUN_PROCESS_IN).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }
    /**
     * Process the command.
     * <br />Step 1 - Process the command if possible and return true if processed.
     * <br />Step 2 - If I can't process, pass to all children (with me as the source).
     * <br />Step 3 - If children didn't process, pass to parent (with me as the source).
     * <br />Note: Never pass to a parent or child that matches the source (to avoid an endless loop).
     * @param strCommand The command to process.
     * @param sourceSField The source screen field (to avoid echos).
     * @param bUseSameWindow If this command creates a new screen, create in a new window?
     * @return true if success.
     */
    public boolean doCommand(String strCommand, ScreenField sourceSField, int bUseSameWindow)
    {
        if ("Start storm".equalsIgnoreCase(strCommand))
            return this.startStorm();
        if ("Stop storm".equalsIgnoreCase(strCommand))
            return this.stopStorm();
        return super.doCommand(strCommand, sourceSField, bUseSameWindow);
    }
    /**
     * StartStorm Method.
     */
    public boolean startStorm()
    {
        Map<String,Object> properties = new HashMap<String,Object>();
        properties.put(DBParams.PROCESS, MessageTrxProcess.class.getName());
        Application app = (Application)this.getTask().getApplication();
        String strQueueName = MessageConstants.TRX_SEND_QUEUE;
        String strQueueType = MessageConstants.INTRANET_QUEUE;
        BaseMessage message = new MapMessage(new TrxMessageHeader(strQueueName, strQueueType, properties), properties);
        String strProcess = Utility.propertiesToURL(null, properties);
        
        if (RunProcessInField.REMOTE_PROCESS.equalsIgnoreCase(this.getScreenRecord().getField(TourEventScreenRecord.RUN_PROCESS_IN).toString()))
        {
            app.getMessageManager().sendMessage(message);
        }
        else if (RunProcessInField.LOCAL_PROCESS.equalsIgnoreCase(this.getScreenRecord().getField(TourEventScreenRecord.RUN_PROCESS_IN).toString()))
        {
            app.getTaskScheduler().addTask(new ProcessRunnerTask(app, strProcess, null));
        }
        else // LOCAL
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

}
