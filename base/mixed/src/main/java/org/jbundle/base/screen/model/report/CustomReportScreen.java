/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.report;

/**
* @(#)Screen.java 0.00 12-Feb-97 Don Corley
*
* Copyright (c) 2009 tourapp.com. All Rights Reserved.
*   don@tourgeek.com
*/
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.GridTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.base.screen.model.util.ReportToolbar;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;


/**
 * This is the base screen for custom reports.
 * You can build a report that prints the controls--as laid out on the report.
 * This class has the ability to set up a custom control to be printed.
 * This is usually used for special format reports, such as labels.
 */
public class CustomReportScreen extends BaseReportScreen
{
    /**
     * Constructor.
     */
    public CustomReportScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public CustomReportScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this.setAppending(false);    // By default

        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
        
        int iErrorCode = this.checkSecurity();
        if ((iErrorCode != DBConstants.NORMAL_RETURN) && (iErrorCode != Constants.READ_ACCESS))
            return;

// Put the grid table in front of this record, so grid operations will work.
        Record gridRecord = this.getMainRecord();
// Even though grid table caches for me, I use the cache to take advantage of multiple reads.
        if (gridRecord != null)
            gridRecord.setOpenMode(gridRecord.getOpenMode() | DBConstants.OPEN_CACHE_RECORDS);    // Cache recently used records.

        BaseTable gridTable = gridRecord.getTable();
        if (!(gridTable instanceof GridTable))
            gridTable = new GridTable(null, gridRecord);
        gridRecord.close();

        this.resizeToContent(this.getTitle());
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Throw up a NAV Toolbar if no toolbars yet.
     * @return The new ReportToolbar.
     */
    public ToolScreen addToolbars()
    {   // Override this to add (call this) or replace (don't call) this default toolbar.
        return new ReportToolbar(null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC);
    }
    /**
     * Setup the print control for this custom report screen.
     * Override this if you don't want a JPanel.
     * @return A component
     */
    public Component setupPrintControl()
    {
        JPanel control = new JPanel();   // Set up the "fake" control to render on print
        control.setOpaque(false);
        control.setLayout(null);
        control.setBounds(0, 0, (int)(7.5 * 72), 10 * 72);
        control.setBackground(Color.WHITE); // Just being careful
        return control;
    }
    /**
     * Set up the physical control (that implements Component).
     */
    public void layoutPrintControl(Component control) // Must o/r
    {
        // Override this to do something
    }
    /**
     * Setup the standard attributes of a component to print.
     */
    public void setupComponent(JComponent component, int x, int y, int width, int height)
    {
        component.setBounds(x, y, width, height);
        component.setBorder(null);
        component.setOpaque(false);
        if (component instanceof JScrollPane)
        {
            ((JScrollPane)component).setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            ((JScrollPane)component).setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            ((JScrollPane)component).getViewport().setOpaque(false);
            component = (JComponent)((JScrollPane)component).getViewport().getView();
            this.setupComponent(component, 0, 0, width, height);
        }
        else
        {
            component.setForeground(Color.black);
            component.setFont(new Font("SansSerif", Font.PLAIN, 12));
            if (component instanceof JTextComponent)
                ((JTextComponent)component).setText("This is the extra text. This is the extra text. This is the extra text. This is the extra text. This is the extra text. This is more extra text This is more extra text This is more extra text This is more extra text this is the last extra text");
            if (component instanceof JTextArea)
            {
                ((JTextArea)component).setWrapStyleWord(true);
                ((JTextArea)component).setLineWrap(true);
            }
        }
    }
}
