/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.util;

/**
 *  WriteJava
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 */
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.Utility;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.SButtonBox;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.thread.TaskScheduler;


/**
 * WriteResourcesButton   
 */
public class WriteResourcesButton extends SButtonBox
{
    public WriteResourcesButton(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int sDisplayFieldDesc)
    {
        super();
        this.init(itsLocation, parentScreen, fieldConverter, sDisplayFieldDesc, "", "Print Resources", null, null, null);
    }
    public int controlToField()
    {
        int iErrorCode = super.controlToField();
        TaskScheduler js = BaseApplet.getSharedInstance().getApplication().getTaskScheduler();
        String strJob = Utility.addURLParam(null, DBParams.SCREEN, DBConstants.ROOT_PACKAGE + "program.util.WriteResources");
        strJob = Utility.addURLParam(strJob, DBParams.TASK, DBConstants.ROOT_PACKAGE + "base.screen.view.swing.control.SApplet"); // Screen class
        js.addTask(strJob);
        return iErrorCode;
    }
}
