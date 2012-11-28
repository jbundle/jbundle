/**
 * @(#)JavaButton.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.screen;

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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.thin.base.thread.*;
import org.jbundle.thin.base.screen.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.app.program.db.*;

/**
 *  JavaButton - .
 */
public class JavaButton extends SButtonBox
{
    protected ClassInfo m_classInfo = null;
    /**
     * Default constructor.
     */
    public JavaButton()
    {
        super();
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?.
     */
    public JavaButton(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int sDisplayFieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, sDisplayFieldDesc);
    }
    /**
     * Initialize class fields.
     */
    public void init(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int sDisplayFieldDesc)
    {
        m_classInfo = null;
        super.init(itsLocation, parentScreen, fieldConverter, sDisplayFieldDesc, "", "Print Java", MenuConstants.PRINT, null, null);
    }
    /**
     * Move the control's value to the field.
     * @return An error value.
     */
    public int controlToField()
    {
        int iErrorCode = super.controlToField();
        if (m_classInfo != null)
        {
            TaskScheduler js = BaseApplet.getSharedInstance().getApplication().getTaskScheduler();
            String strJob = Utility.addURLParam(null, DBParams.SCREEN, ".app.program.manual.util.WriteClasses");
            strJob = Utility.addURLParam(strJob, "fileName", m_classInfo.getField(ClassInfo.CLASS_SOURCE_FILE).toString());
            strJob = Utility.addURLParam(strJob, "package", m_classInfo.getField(ClassInfo.CLASS_PACKAGE).toString());
            strJob = Utility.addURLParam(strJob, "project", Converter.stripNonNumber(m_classInfo.getField(ClassInfo.CLASS_PROJECT_ID).toString()));
            strJob = Utility.addURLParam(strJob, DBParams.TASK, DBConstants.SAPPLET); // Screen class
            js.addTask(strJob);
        //BasePanel parentScreen = Screen.makeWindow(this.getParentScreen().getTask().getApplication());
        //WriteJava screen = new WriteJava(null, null, parentScreen, null, ScreenConstants.DISPLAY_FIELD_DESC, m_classInfo.getField(ClassInfo.CLASS_SOURCE_FILE), m_classInfo.getField(ClassInfo.CLASS_PACKAGE));
        //screen.writeFileDesc();     // Write the code
        //BasePanel panel = screen.getRootScreen();
        //panel.free();
        }
        return iErrorCode;
    }
    /**
     * SetClassInfo Method.
     */
    public void setClassInfo(ClassInfo classInfo)
    {
        m_classInfo = classInfo;
    }

}
