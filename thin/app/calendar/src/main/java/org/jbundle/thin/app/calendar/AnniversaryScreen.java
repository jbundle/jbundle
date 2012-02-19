/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.calendar;

/**
 * OrderEntry.java:   Applet
 *  Copyright � 1997 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.Container;

import javax.swing.JComponent;

import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JScreen;
import org.jbundle.thin.base.screen.JScreenToolbar;
import org.jbundle.thin.main.calendar.db.AnnivMaster;
import org.jbundle.thin.main.calendar.db.Anniversary;


/**
 * Main Class for applet OrderEntry
 */
public class AnniversaryScreen extends JScreen
{
	private static final long serialVersionUID = 1L;
    
    /**
     *  OrderEntry Class Constructor.
     */
    public AnniversaryScreen()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public AnniversaryScreen(Object parent, Object obj)
    {
        this();
        this.init(parent, obj);
    }
    /**
     * The init() method is called by the AWT when an applet is first loaded or
     * reloaded.  Override this method to perform whatever initialization your
     * applet needs, such as initializing data structures, loading images or
     * fonts, creating frame windows, setting the layout manager, or adding UI
     * components.
     */
    public void init(Object parent, Object obj)
    {
        super.init(parent, obj);
        if (this.getFieldList().getTable() == null)
        {
            RemoteSession remoteSession = ((BaseApplet)parent).makeRemoteSession(null, ".main.calendar.remote.AnniversaryTableSession");
            ((BaseApplet)parent).linkRemoteSessionTable(remoteSession, this.getFieldList(), false);
        }
    }
    /**
     * Build the list of fields that make up the screen.
     */
    public FieldList buildFieldList()
    {
        return new AnnivMaster(null); // If overriding class didn't set this
    }
    /**
     * Add the toolbars?
     */
    public JComponent createToolbar()
    {
        return new JScreenToolbar(this, null);
    }
    /**
     * Add this label to the first column of the grid.
     */
    public JComponent addScreenLabel(Container parent, Converter fieldInfo)
    {
        return super.addScreenLabel(parent, fieldInfo);
    }
    /**
     * Add the screen controls to the second column of the grid.
     */
    public JComponent addScreenControl(Container parent, Converter fieldInfo)
    {
        return super.addScreenControl(parent, fieldInfo);
    }
    /**
     * Add the screen controls to the second column of the grid.
     * Create a default component for this fieldInfo.
     * @param fieldInfo the field to create a control for.
     * @return The component.
     */
    public JComponent createScreenComponent(Converter fieldInfo)
    {
        if ((Anniversary.START_DATE_TIME.equalsIgnoreCase(fieldInfo.getFieldName()))
            || (Anniversary.END_DATE_TIME.equalsIgnoreCase(fieldInfo.getFieldName())))
                return new org.jbundle.thin.base.screen.util.cal.JCalendarDualField(fieldInfo);
        return super.createScreenComponent(fieldInfo);
    }
}
