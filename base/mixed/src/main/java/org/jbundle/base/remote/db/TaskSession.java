package org.jbundle.base.remote.db;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.Unreferenced;
import java.util.HashMap;
import java.util.Map;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.db.netutil.NetUtility;
import org.jbundle.base.message.record.RecordMessageConstants;
import org.jbundle.base.remote.BaseSession;
import org.jbundle.base.remote.BaseTaskSession;
import org.jbundle.base.remote.message.ReceiveQueueSession;
import org.jbundle.base.remote.message.SendQueueSession;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.DatabaseOwner;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.base.util.MenuConstants;
import org.jbundle.base.util.Utility;
import org.jbundle.main.user.db.UserInfo;
import org.jbundle.main.user.screen.SetupNewUserHandler;
import org.jbundle.model.DBException;
import org.jbundle.model.Service;
import org.jbundle.thin.base.db.Params;
import org.jbundle.thin.base.db.mem.base.PDatabase;
import org.jbundle.thin.base.db.mem.base.PTable;
import org.jbundle.thin.base.db.mem.proxy.YDatabase;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabaseOwner;
import org.jbundle.thin.base.db.model.ThinPhysicalTableOwner;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.message.BaseMessageSender;
import org.jbundle.thin.base.remote.RemoteReceiveQueue;
import org.jbundle.thin.base.remote.RemoteSendQueue;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.util.Application;
import org.jbundle.thin.base.util.ThinUtil;


/**
 * A RemoteTaskSessionObject is the general-purpose Task session.
 * The remote objects for a typical application do not mirror the client application.
 * Usually, a RemoteTask session is started and the databases and tables are started
 * under the RemoteTaskSession to handle only the remote table calls.
 * If specific sessions are created, they can all be created under this RemoteTaskSession.
 * There should not be any issues with multi-tasking as this class is thread-safe
 * (and rarely called from recordowners). The only issue to be aware of, is remoteSessions
 * are not automatically freed, so you need to free them yourself in the client code.
 */
public class TaskSession extends BaseTaskSession
    implements RemoteTask, Unreferenced,   // Must implement RemoteTask's remote calls!
    	ThinPhysicalDatabaseOwner, ThinPhysicalTableOwner
{
    private static final long serialVersionUID = 1L;

    /**
     * My peer task as set by the remote session.
     * NOTE: I do not need any elaborate code to clear this if the message session is freed,
     * because I only use this reference as a filter and I never do a remote call against it.
     */
    protected RemoteTask m_messageTaskPeer = null;

    /**
     * Build a new remote task session.
     */
    public TaskSession() throws RemoteException
    {
        super();
    }
    /**
     * Build a new remote task session.
     * @param application Parent application.
     */
    public TaskSession(Service application) throws RemoteException
    {
        this();
        m_application = (Application)application;    // Don't pass down, because init matched standard session init.
        this.init(null, null, null);
    }
    /**
     * Constructor.
     * @param parentSessionObject Parent that created this session object (usually null for task sessions).
     * @param record Main record for this session  (always null for task sessions).
     * @param objectID ObjectID of the object that this SessionObject represents  (usually null for task sessions).
     */
    public void init(BaseSession parentSessionObject, Record record, Object objectID)
    {
        super.init(parentSessionObject, record, objectID);
    }
    /**
     * Free this object and all sub-objects.
     */
    public void free()
    {
        try {
            this.setRemoteMessageTask(null);
        } catch (RemoteException ex)    {
            // Never
        }
        super.free();
    }
    /**
     * Free the objects.
     * This method is called from the remote client, and frees this session.
     */
    public void freeRemoteSession() throws RemoteException
    {
        super.freeRemoteSession();
    } 
    /**
     * Called by the RMI runtime sometime after the runtime determines that
     * the reference list, the list of clients referencing the remote object,
     * becomes empty.
     * @since JDK1.1
     */
    public void unreferenced()
    {
        Utility.getLogger().info("unreferenced()");
        this.free();
    }
    /**
     * Get this task's environment.
     * @return The environment.
     */
    public Environment getEnvironment()
    {
        Environment env = ((BaseApplication)this.getApplication()).getEnvironment();
        if (env == null)
            env = Environment.getEnvironment(null);   // Never
        return env;
    }
    /**
     * Build a new remote session and initialize it.
     * NOTE: This is convenience method to create a task below the current APPLICATION (not below this task)
     * @param properties to create the new remote task
     * @return The remote Task.
     */
    public RemoteTask createRemoteTask(Map<String, Object> properties) throws RemoteException
    {
        try   {
            Application app = this.getApplication();
            RemoteTask remoteServer = new TaskSession(app);
            ((TaskSession)remoteServer).setProperties(properties);
            return remoteServer;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    /**
     * Make a table for this database.
     * @param strRecordClassName The record class name.
     * @param strTableSessionClassName The (optional) session name for the table.
     * @param properties The properties for the remote table.
     * @param remoteDB The remote db to add this new table to.
     */
    public RemoteTable makeRemoteTable(String strRecordClassName, String strTableSessionClassName, Map<String, Object> properties, Map<String, Object> propDatabase) throws RemoteException
    {
        Record record = (Record)ThinUtil.getClassService().makeObjectFromClassName(strRecordClassName);
        if (record == null)
            return null;
        if (strTableSessionClassName == null)
            this.setMasterSlave(RecordOwner.SLAVE); // If no table is specified, TableSession is a slave
        if (properties != null)
            if (properties.get(RecordMessageConstants.TABLE_NAME) != null)
                record.setTableNames((String)properties.get(RecordMessageConstants.TABLE_NAME));
        
        Map<String,Object> propOld = this.getProperties();
        this.setProperties(propDatabase);   // This will cause the database owner to be correct (App by default, this if AUTO_COMMIT is off)
        Map<String,Object> propDBOld = this.addDatabaseProperties(propDatabase, this.getDatabaseOwner());
        BaseDatabase database = null;
        if (this.getDatabaseOwner() != null)
        {   // Always - Make sure the database is set up correctly BEFORE I create the record.
            database = this.getDatabaseOwner().getDatabase(record.getDatabaseName(), record.getDatabaseType(), null);
            if (database != null)
            {   // Rare - only on querytables, then the database will have already been set up.
                database.setMasterSlave(RecordOwner.SLAVE);      // Don't create client behaviors
                if (propDatabase != null)
                {
                    propDatabase.remove(DBParams.MESSAGES_TO_REMOTE);   // Don't copy these down
                    propDatabase.remove(DBParams.CREATE_REMOTE_FILTER);
                    propDatabase.remove(DBParams.UPDATE_REMOTE_FILTER);
                    database.getProperties().putAll(propDatabase);    // Add these properties to the current db properties.
                }
            }
        }
        record.init(this);
        if (database == null)
        {   // Better late then never
            database = record.getTable().getDatabase();
            if (database != null)
            {   // Rare - only on querytables, then the database will have already been set up.
                database.setMasterSlave(RecordOwner.SLAVE);      // Don't create client behaviors
                if (propDatabase != null)
                    database.getProperties().putAll(propDatabase);    // Add these properties to the current db properties.
            }            
        }
        this.setProperties(propOld);
        this.getDatabaseOwner().setProperties(propDBOld);
        this.setMasterSlave(-1);    //Back to default
        RemoteTable remoteTable = null;
        if (strTableSessionClassName != null)
        	remoteTable = (TableSession)ThinUtil.getClassService().makeObjectFromClassName(strTableSessionClassName);
        try   {
            if (remoteTable == null)
                remoteTable = new TableSession();
            this.removeRecord(record);  // Record should belong to the TableSessionObject. (I just added it to this, so it could access the environment)
            ((TableSession)remoteTable).init(this, record, null);
        } catch (Exception ex)  {
            remoteTable = null;
        }
        if (remoteTable != null)
            if (properties != null)
            {
                for (String strName : properties.keySet())
                {
                    String strValue = (String)properties.get(strName);
                    try {
                        remoteTable.setRemoteProperty(strName, strValue);
                    } catch (RemoteException ex)    {
                        // Never
                    }
                }
            }
        return remoteTable;        
    }
    /**
     * addDatabaseProperties
     * @param properties
     * @return The old properties
     */
    public Map<String, Object> addDatabaseProperties(Map<String,Object> properties, DatabaseOwner databaseOwner)
    {
        Map<String,Object> oldProperties = databaseOwner.getProperties();
        if (properties != null)
        {   // Also move any application params up.
            for (String key : properties.keySet())
            {   // Move these params to the application
                if ((key.endsWith(BaseDatabase.DBSHARED_PARAM_SUFFIX))
            		|| (key.endsWith(BaseDatabase.DBUSER_PARAM_SUFFIX))
                    || (DBConstants.DB_USER_PREFIX.equals(key))
                        || (DBConstants.SUB_SYSTEM_LN_SUFFIX.equals(key)))
                {
                    if (oldProperties == databaseOwner.getProperties())
                    {   // Return the old properties
                        oldProperties = new HashMap<String,Object>();
                        oldProperties.putAll(databaseOwner.getProperties());
                    }
                    databaseOwner.setProperty(key, (String)properties.get(key));
                }
            }
        }
        return oldProperties;
    }
    /**
     * Create the remote send queue.
     * @param strQueueName The queue name to service.
     * @param strQueueType The queue type.
     * @return The RemoteSendQueue.
     */
    public RemoteSendQueue createRemoteSendQueue(String strQueueName, String strQueueType) throws RemoteException
    {
        BaseMessageManager messageManager = this.getEnvironment().getMessageManager(this.getApplication(), true);
        BaseMessageSender sender = (BaseMessageSender)messageManager.getMessageQueue(strQueueName, strQueueType).getMessageSender();
        SendQueueSession remoteQueue = new SendQueueSession(this, sender);
        return remoteQueue;
    }
    /**
     * Create the remote receive queue.
     * @param strQueueName The queue name to service.
     * @param strQueueType The queue type.
     * @return The RemoteSendQueue.
     */
    public RemoteReceiveQueue createRemoteReceiveQueue(String strQueueName, String strQueueType) throws RemoteException
    {
        BaseMessageManager messageManager = this.getEnvironment().getMessageManager(this.getApplication(), true);
        BaseMessageReceiver receiver = (BaseMessageReceiver)messageManager.getMessageQueue(strQueueName, strQueueType).getMessageReceiver();
        ReceiveQueueSession remoteQueue = new ReceiveQueueSession(this, receiver);
        return remoteQueue;
    }
    /**
     * Log this task in under this (new) user.
     * @param strUserName The user to log this task in under
     * @param strPassword The password (NOTE: this is encrypted - do not send clear text).
     * @exception Throw an exception if logon is not successful.
     * @return The security map for this user
     */
    public Map<String,Object> login(String strUserName, String strPassword, String strDomain) throws RemoteException
    {
        MainApplication app = (MainApplication)this.getApplication();
        int iErrorCode = app.login(this, strUserName, strPassword, strDomain);
        if (iErrorCode != DBConstants.NORMAL_RETURN)
            throw new RemoteException(this.getLastError(iErrorCode));
        Map<String,Object> mapReturnParams = new HashMap<String,Object>();
        mapReturnParams.put(Params.SECURITY_MAP, app.getProperty(Params.SECURITY_MAP));
        Map<String,Object>userProperties = new HashMap<String,Object>();
        if (app.getSystemRecordOwner().getProperties() != null)
            userProperties.putAll(app.getSystemRecordOwner().getProperties());
        else
        {   // Never
            userProperties.put(DBParams.USER_NAME, this.getProperty(DBParams.USER_NAME));
            userProperties.put(DBParams.USER_ID, this.getProperty(DBParams.USER_ID));
        }
        if (this.getProperty(DBParams.AUTH_TOKEN) != null)
            userProperties.put(DBParams.AUTH_TOKEN, this.getProperty(DBParams.AUTH_TOKEN));
        if (userProperties.get(DBParams.JAVA) != null)
            userProperties.put("javaApplet", userProperties.get(DBParams.JAVA));    // "java" is a reserved word in js
        mapReturnParams.put(Params.USER_PROPERTIES, userProperties);        
        return mapReturnParams;
    }
    /**
     * Do a remote action.
     * @param strCommand Command to perform remotely.
     * @return boolean success.
     */
    public Object doRemoteAction(String strCommand, Map<String, Object> properties) throws RemoteException, DBException
    {
        if ((MenuConstants.CHANGE_PASSWORD.equalsIgnoreCase(strCommand)) && (properties != null))
        {
            UserInfo recUserInfo = new UserInfo(this);
            Object objErrorCode = null;
            try {
                String strUserName = (String)properties.get(Params.USER_NAME);
                if (strUserName == null)
                    strUserName = (String)properties.get(Params.USER_ID);
                String strCurrentPassword = (String)properties.get(Params.PASSWORD);
                String strNewPassword = (String)properties.get("newPassword");
                if (recUserInfo.getUserInfo(strUserName, false) == false)
                    throw new RemoteException(this.getString("Error, user name is incorrect."));
                if ((recUserInfo.getField(UserInfo.kPassword).isNull()) || (!recUserInfo.getField(UserInfo.kPassword).toString().equals(strCurrentPassword)))
                    throw new RemoteException(this.getString("Error, current password was incorrect."));
                if ((strNewPassword == null) || (strNewPassword.length() == 0))
                    throw new RemoteException(this.getString("Error, new password can't be empty."));
                recUserInfo.edit();
                recUserInfo.getField(UserInfo.kPassword).setString(strNewPassword);
                recUserInfo.set();
                objErrorCode = new Integer(DBConstants.NORMAL_RETURN);  // Success!
            } catch (DBException ex) {
                throw ex;
            } finally {
                recUserInfo.free();
            }
            return objErrorCode;
        }
        else if ((MenuConstants.CREATE_NEW_USER.equalsIgnoreCase(strCommand)) && (properties != null))
        {
            UserInfo recUserInfo = new UserInfo(this);
            recUserInfo.addListener(new SetupNewUserHandler(null));
            Object objErrorCode = null;
            try {
                String strUserName = (String)properties.get(Params.USER_NAME);
                String strPassword = (String)properties.get(Params.PASSWORD);
                if (recUserInfo.getUserInfo(strUserName, false) == true)
                    throw new RemoteException(this.getString("Error, user name already exists."));
                recUserInfo.addNew();
                recUserInfo.getField(UserInfo.kUserName).setString(strUserName);
                recUserInfo.getField(UserInfo.kPassword).setString(strPassword);
                recUserInfo.add();
                objErrorCode = new Integer(DBConstants.NORMAL_RETURN);  // Success!
            } catch (DBException ex) {
                throw ex;
            } finally {
                recUserInfo.free();
            }
            return objErrorCode;
        }
        else if ((DBParams.TABLE_PATH.equalsIgnoreCase(strCommand)) && (properties != null))
        {
            //String dataType = (String)properties.get(YDatabase.DATA_TYPE);
            String recordClass = (String)properties.get(YDatabase.RECORD_CLASS);
            String tableName = (String)properties.get(YDatabase.TABLE_NAME);
            String language = (String)properties.get(Params.LANGUAGE);
        	
            try {
            	ByteArrayOutputStream ostream = new ByteArrayOutputStream();
				NetUtility.getNetTable(recordClass, tableName, language, this, this, this, ostream);
				return ostream.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return super.doRemoteAction(strCommand, properties);
    }
    /**
     * Set the task that handles messages for this remote session.
     * This is optional, it keeps remote file messages from being send to the client.
     * If you set this, messages generated by remote ClientTable calls will not be sent back up
     * to their client (by setting this remotetask as the client filter).
     * @param messageTask The remote message task that handles messages for this remote task.
     */
    public void setRemoteMessageTask(RemoteTask messageTaskPeer) throws RemoteException
    {
        m_messageTaskPeer = messageTaskPeer;    // Set it.
    }
    /**
     * Set the task that handles messages.
     * This is optional, it keeps remote file messages from being send to the client.
     * @param messageTask The remote message task that handles messages for this remote task.
     */
    public RemoteTask getRemoteMessageTask()
    {
        return m_messageTaskPeer;
    }
    /**
     * Set the ptable that I am an owner of.
     * @param pTable
     */
    public void setPTable(PTable pTable)
    {
    }
    /**
     * Set the pdatabase that I am an owner of.
     * @param pTable
     */
    public void setPDatabase(PDatabase pDatabase)
    {
    }
}
