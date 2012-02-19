/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.cal.grid;

/**
 * OrderEntry.java:   Applet
 *  Copyright � 1997 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;

import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.screen.JBasePanel;
import org.jbundle.thin.base.screen.JBaseScreen;
import org.jbundle.util.calendarpanel.CalendarPanel;
import org.jbundle.util.calendarpanel.model.CalendarModel;


/**
 * A Basic order entry screen.
 * This screen is made of a panel with a GridBagLayout. Labels in the first column, aligned right.
 * Data fields in the second column aligned left.
 */
public class JCalendarScreen extends JBaseScreen
{
    private static final long serialVersionUID = 1L;

    protected CalendarModel m_model = null;

    /**
     *  JBaseScreen Class Constructor.
     */
    public JCalendarScreen()
    {
        super();
    }
    /**
     *  JBaseScreen Class Constructor.
     * Typically, you pass the BaseApplet as the parent, and the record or GridTableModel as the parent.
     */
    public JCalendarScreen(Object parent, Object record)
    {
        this();
        this.init(parent, record);
    }
    /**
     * Initialize this class.
     */
    public void init(Object parent, Object record)
    {
        super.init(parent, record);
        record = this.getFieldList();   // Created/set in JBaseScreen.init
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        FieldTable table = ((FieldList)record).getTable();
        CalendarModel model = this.getCalendarModel(table);
        ImageIcon backgroundImage = null;
        if (this.getBaseApplet() != null)
        	backgroundImage = this.getBaseApplet().getBackgroundImage();	// Calendar panel is transparent, but this helps with rendering see-thru components 
        CalendarPanel panel = new CalendarPanel(model, true, backgroundImage);
        this.add(panel);
    }
    /**
     * Free the sub=components.
     */
    public void free()
    {
        CalendarPanel panel = (CalendarPanel)JBasePanel.getSubScreen(this, CalendarPanel.class);
        if (panel != null)
            panel.free();
        super.free();
    }
    /**
     * Get the calendar model.
     * Override this to supply the calendar model.
     * The default listener wraps the table in a CalendarThinTableModel.
     */
    public CalendarModel getCalendarModel(FieldTable table)
    {
        if (m_model == null)
            if (table != null)
                m_model = new CalendarThinTableModel(table);
        return m_model;
    }
    /**
     * Build the list of fields that make up the screen.
     */
    public FieldList buildFieldList()
    {
        return null;    // Override
    }
    /**
     * Add the scrollbars?
     * @return Defaults to yes, override for no.
     */
    public boolean isAddScrollbars()
    {
        return true;
    }
}
