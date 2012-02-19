/*
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.remote.server;

import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.Utility;
import org.jbundle.base.remote.db.TaskSession;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.model.App;
import org.jbundle.model.db.Rec;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.message.MessageConstants;
import org.jbundle.thin.base.remote.ApplicationServer;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.RemoteObject;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.util.Application;
import org.jbundle.util.osgi.finder.ClassServiceUtility;

/** 
 * RmiSessionServer - The remote server.
 * You will need to start this Server with a command such as this:
 * <pre>
 * java -Djava.rmi.server.codebase=http://209.85.16.220/classes/
 * -Djava.security.policy=/data/java/tour/bin/policy.all org.jbundle.base.remote.rmiserver.RmiServerSession "local=Jdbc remote=Jdbc"
 * </pre>
 * @author  Administrator
 * @version 1.0.0
 */
public class RemoteSessionServer extends RemoteObject
    implements ApplicationServer
{
    private static final long serialVersionUID = 1L;

    /**
     * The main application associated with this server.
     */
    protected App m_app = null;

    /**
     * Creates new RmiSessionServer.
     */
    public RemoteSessionServer() throws RemoteException
    {
        super();
    }
    /**
     * Creates new RmiSessionServer.
     */
    public RemoteSessionServer(App app, Rec record, Map<String,Object> properties) throws RemoteException
    {
        this();
        this.init(app, record, properties);
    }
    /**
     * Creates new RmiSessionServer.
     */
    public void init(App app, Rec record, Map<String,Object> properties)
    {
        m_app = app;
    }
    /**
     * Start up the remote server.
     * @param properties The properties to use on setup.
     */
    public static RemoteSessionServer startupServer(Map<String,Object> properties)
    {
    	RemoteSessionServer remoteServer = null;
        Utility.getLogger().info("Starting RemoteSession server");

        try {
            remoteServer = new RemoteSessionServer(null, null, null);
        } catch (RemoteException ex)    {
            ex.printStackTrace();
        }

        return remoteServer;
    }
    /**
     * Start up the remote server.
     * Params:
     * <br/>local=[(Client)|Jdbc|Net|Memory]  // Type of db to use for local tables
     * <br/>remote=[(Client)|Jdbc|Net|Memory] // Type of db to use for remote tables
     * <br/>table=[Client|Jdbc|(Net)|Memory]  // Type of db to use for table tables
     * <br/>freeifdone=[(true)|false]         // When there are no apps, shut down?
     * <br/>messagefilter=[(sequential)|tree] // Type of message filter
     * <br/>provider=[serverurl][:serverport] // remote RMI Server to hook to (defaults to localhost:1099)
     * <br/>remoteappname=[appname]             // remote RMI app server name (defaults to appserver) (msgapp for the messageapp)
     * <br/>lockprovider=[appname]            // remote RMI lock server name (defaults to lockserver)
     * <br/>appname=[appname]                 // My RMI app name (clients contact me with this app name)
     * <br/>jmsserver=[true|(false)]          // Am I the XML web server?
     */
    public static void main(String[] args)
    {
        Map<String,Object> propertiesTemp = new Hashtable<String,Object>();
        Util.parseArgs(propertiesTemp, args);
        if (propertiesTemp.get(DBParams.FREEIFDONE) == null)
            propertiesTemp.put(DBParams.FREEIFDONE, DBConstants.FALSE);   // Don't free when only the last app is running.
        if (propertiesTemp.get(DBParams.LOCAL) == null)
            propertiesTemp.put(DBParams.LOCAL, DBParams.JDBC); // Remote Server ALWAYS uses Jdbc
        if (propertiesTemp.get(DBParams.REMOTE) == null)
            propertiesTemp.put(DBParams.REMOTE, DBParams.JDBC);
        if (propertiesTemp.get(DBParams.TABLE) == null)
            propertiesTemp.put(DBParams.TABLE, DBParams.JDBC);
        if (propertiesTemp.get(MessageConstants.MESSAGE_FILTER) == null)
            propertiesTemp.put(MessageConstants.MESSAGE_FILTER, MessageConstants.TREE_FILTER);  // Default for a server

        RemoteSessionServer remoteServer = null;
        try {
            remoteServer = new RemoteSessionServer(null, null, propertiesTemp);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Environment env = new Environment(propertiesTemp);
        // Note the order that I do this... this is because MainApplication may need access to the remoteapp during initialization
        BaseApplication app = new MainApplication();
        remoteServer.init(app, null, null);     // Okay to call init twice here
        Map<String,Object> properties = null;
        if (args != null)
        {
            properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
        }
        app.init(env, properties, null); // Default application (with params).
    }
    /**
     * Build a new remote session and initialize it.
     * @return The remote Task.
     */
    public RemoteTask createRemoteTask(Map<String, Object> properties) throws RemoteException
    {
        Utility.getLogger().info("createRemoteTask()");
        RemoteTask remoteTask = this.getNewRemoteTask(properties);
        return remoteTask;
    }
    /**
     * Get a remote session from the pool and initialize it.
     * If the pool is empty, create a new remote session.
     * @param strUserID The user name/ID of the user, or null if unknown.
     * @return The remote Task.
     */
    public RemoteTask getNewRemoteTask(Map<String,Object> properties)
    {
        try   {
            Application app = new MainApplication(((BaseApplication)m_app).getEnvironment(), properties, null);
            RemoteTask remoteServer = new TaskSession(app);
            ((TaskSession)remoteServer).setProperties(properties);
            return remoteServer;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    /**
     * Shutdown the server.
     */
    public void shutdown()
    {
        Environment env = ((BaseApplication)m_app).getEnvironment();
        if (m_app != null)
        	m_app.free();
        m_app = null;
        ClassServiceUtility.getClassService().shutdownService(this);
        if (env != null)
        	env.freeIfDone();
//x        System.exit(0);
    }
}
