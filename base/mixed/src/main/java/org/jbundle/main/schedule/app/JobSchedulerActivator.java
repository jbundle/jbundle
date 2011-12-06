/*
 * RmiSessionServer.java
 *
 * Created on January 10, 2000, 4:47 PM
 
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.main.schedule.app;

import java.util.Map;

import org.jbundle.base.util.Environment;
import org.jbundle.thin.base.util.Application;

public class JobSchedulerActivator extends BaseAppActivator
{
    /**
     * Start this service.
     * Override this to do all the startup.
     * @return true if successful.
     */
    public Application startupThisApp(Environment env, Map<String, Object> props)
    {
    	return new JobSchedulerApp(env, props, null);
    }
}

