/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.status;

import java.util.HashMap;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.filter.StringSubFileFilter;
import org.jbundle.base.model.Utility;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.main.db.base.BaseStatus;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.thin.base.thread.AutoTask;
import org.jbundle.thin.base.util.Application;

/**
 * This is a convenience class, so users don't have to remember the path to SApplet.
 */
public class Main extends BaseProcess
{
	private static final long serialVersionUID = 1L;

	/**
     * Constructor.
     */
    public Main()
    {
        super();
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public Main(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        m_properties = properties;

        super.init(taskParent, recordMain, properties);
    }
    /**
     * Free the resources.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Run the code in this process (you must override).
     */
    public void run()
    {
    	try {
			System.out.println("-------------------------Starting test-----------------------");
			
			BaseStatus record = new BaseStatus(this);
			
			record.addListener(new StringSubFileFilter("D", BaseStatus.DESCRIPTION, null, null, null, null));
			
			record.setKeyArea(BaseStatus.DESCRIPTION_KEY);
			record.close();
			
			while (record.hasNext())
			{
				record.next();
				
				System.out.println("+++ " + record.getField(BaseStatus.ID) + " " + record.getField(BaseStatus.DESCRIPTION));
				
			}
			
			System.out.println("-------------------------Ending test-----------------------");
		} catch (DBException e) {
			e.printStackTrace();
		}
    }
    /**
     * Start this program.
     * server=rmiserver
     * codebase=webserver
     */
    public static void main(String args[])
    {
    	Map<String,Object> properties = new HashMap<String,Object>();
    	properties = Utility.parseArgs(properties, args);
        Environment env = new Environment(properties);
        Application app = new MainApplication(env, properties, null);
        Task task = new AutoTask(app, null, null);
        Main test = new Main(task, null, null);
        test.run();
//        org.jbundle.Main.main(args);
    }

}
