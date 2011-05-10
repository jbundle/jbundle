/*
 * Task to check the inbox for incomming automated requests.
 */

package org.jbundle.base.message.trx.transport.email;

import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.MainApplication;
import org.jbundle.model.Service;
import org.jbundle.thin.base.util.Application;
import org.jbundle.thin.base.util.Util;


/**
 * Servlet that receives SOAP messages.
 */
public class MessageReceivingPopClient extends ProcessRunnerTask
{

    /**
     * Initialization.
     */
    public MessageReceivingPopClient()
    {
        super();
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public MessageReceivingPopClient(Service application, String strParams, Map<String,Object> properties)
    {
        this();
        this.init(application, strParams, properties);
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public void init(Service application, String strParams, Map<String, Object> properties)
    {
        super.init(application, strParams, properties);
    }
    /**
     * Free the resources.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Run this stand-alone.
     */
    public static void main(String args[])
    {
        Map<String,Object> properties = null;
        if (args != null)
        {
            properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
        }
        BaseApplication app = new MainApplication(null, properties, null);
        app.getTaskScheduler().addTask(new MessageReceivingPopClient());
    }
    /**
     * Run the code in this process.
     */
    public void runTask()
    {
        this.setProperty(DBParams.PROCESS, MessageReceivingPopClientProcess.class.getName());
        
        super.runTask();
    }
}
