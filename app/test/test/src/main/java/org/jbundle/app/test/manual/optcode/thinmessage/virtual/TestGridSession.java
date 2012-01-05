/*
 *  @(#)RequestSession.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.manual.optcode.thinmessage.virtual;

import java.util.Map;
import java.util.Properties;

import org.jbundle.app.test.test.db.TestTable;
import org.jbundle.base.db.GridTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.message.opt.AutoRecordMessageListener;
import org.jbundle.base.remote.db.TaskSession;
import org.jbundle.base.remote.opt.TableModelSession;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageHeader;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.message.MapMessage;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.util.Application;


/**
 *  RequestSession - Remote side of the thin brochure request session.
 */
public class TestGridSession extends TableModelSession
{
    /**
     *  Default constructor.
     */
    public TestGridSession() throws RemoteException
    {
        super();
    }
    /**
     *  Constructor.
     */
    public TestGridSession(TaskSession parentSessionObject) throws RemoteException
    {
        this();
        this.init(parentSessionObject);
    }
    /**
     *  Initialize class fields.
     */
    public void init(TaskSession parentSessionObject)
    {
        super.init(parentSessionObject, null, null);
    }
    /**
     * Add behaviors to this session.
     */
    public void addListeners()
    {
        super.addListeners();
        Record record = this.getMainRecord();
        if (!(record.getTable() instanceof GridTable))
        {
            GridTable gridTable = new GridTable(null, record);
            gridTable.setCache(true);  // Typically, the client is a gridscreen which caches the records (so I don't have to!)
        }
        
        this.selectGridFields();    // Initial value
        
        BaseMessageManager messageManager = ((Application)this.getTask().getApplication()).getMessageManager();
        Integer intRegistryID = null;
        if (messageManager != null)
        {
            Object source = this;
            BaseMessageFilter messageFilter = new BaseMessageFilter(MessageConstants.TRX_RETURN_QUEUE, MessageConstants.INTERNET_QUEUE, source, null);
            messageManager.addMessageFilter(messageFilter);
            new AutoRecordMessageListener(null, record, false, messageFilter);
        }
    }
    /**
     *  Override this to do an action sent from the client.
     */
    public Object doRemoteCommand(String strCommand, Map<String, Object> properties) throws RemoteException, DBException
    {
        if (strCommand.equals(Constants.RESET))
        {
            Map map = new Properties();
            map.put(DBConstants.STRING_OBJECT_ID_HANDLE, new Integer(7));
            map.put(DBParams.FIELD, "TestVirtual");
            map.put(DBParams.VALUE, "123");
            BaseMessage message = new MapMessage(new BaseMessageHeader(MessageConstants.TRX_RETURN_QUEUE, MessageConstants.INTERNET_QUEUE, null, null), map);

            BaseMessageManager messageManager = ((Application)this.getTask().getApplication()).getMessageManager();
            messageManager.sendMessage(message);
            
            return Boolean.TRUE;
        }
        return super.doRemoteCommand(strCommand, properties);
    }
    /**
     *  Override this to open the main file for this session.
     */
    public Record openMainRecord()
    {
        return new TestTable(this);
    }
    /**
     * Select the fields required for the grid screen.
     */
    public void selectGridFields()
    {
        Record record = this.getMainRecord();
        
        record.setSelected(false);
        record.getField(TestTable.kID).setSelected(true);
        record.getField(TestTable.kTestName).setSelected(true);
        record.getField(TestTable.kTestCurrency).setSelected(true);
        record.getField(TestTable.kTestVirtual).setSelected(true);
    }
}
