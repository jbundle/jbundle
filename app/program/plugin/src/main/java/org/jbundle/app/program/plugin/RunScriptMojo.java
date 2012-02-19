/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.plugin;

import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.jbundle.app.program.script.data.importfix.base.RunScriptProcess;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.Utility;
import org.jbundle.base.util.Environment;
import org.jbundle.model.Task;
import org.jbundle.thin.base.screen.ThinApplication;
import org.jbundle.thin.base.thread.AutoTask;
import org.jbundle.thin.base.util.Application;

/**
 * Goal which runs a script using the program script utility.
 *
 * @goal run-script
 * 
 * @phase process-sources
 */
public class RunScriptMojo
    extends AbstractMojo
{
    /**
     * My Properties.
     *
     * @parameter
     */
    private Map properties;

    /**
     * Execute the script mojo.
     */
    public void execute() throws MojoExecutionException
    {
        if (properties == null)
        {
            getLog().warn("run-script failed: No properties supplied");
            return;
        }
        boolean bSuccess = false;
        Environment env = null;
        RunScriptProcess process = null;
        try {
            env = new Environment(properties);
            Application app = new ThinApplication(env, properties, null);
            Task task = new AutoTask(app, null, null);
            process = new RunScriptProcess(task, null, null);
            bSuccess = (process.doRunCommand(null, properties) == DBConstants.NORMAL_RETURN);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null)
                process.free();
            if (env != null)
                env.free();
        }
        if (bSuccess)
            getLog().info("run-script ran successfully: " + properties.get(DBParams.PROCESS));
        else
            getLog().warn("run-script failed: " + properties.get(DBParams.PROCESS));
    }
}
