/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.nettable;

/**
 * OrderEntry.java:   Applet
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.Container;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.jbundle.model.DBException;
import org.jbundle.thin.app.test.test.db.TestTable;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.db.mem.base.PTable;
import org.jbundle.thin.base.db.mem.base.PhysicalDatabaseParent;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabase;
import org.jbundle.thin.base.screen.AbstractThinTableModel;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseFrame;
import org.jbundle.thin.base.screen.JBaseScreen;
import org.jbundle.thin.base.screen.ThinApplication;
import org.jbundle.thin.base.screen.grid.JCellButton;
import org.jbundle.thin.base.util.Application;


/**
 * Main Class for applet OrderEntry
 */
public class TestThinGridApplet extends BaseApplet
    implements ActionListener
{
    private static final long serialVersionUID = 1L;

    /**
     *  OrderEntry Class Constructor.
     */
    public TestThinGridApplet()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public TestThinGridApplet(String[] args)
    {
        this();
        this.init(args);
    }
    /**
     * The getAppletInfo() method returns a string describing the applet's
     * author, copyright date, or miscellaneous information.
     */
    public String getAppletInfo()
    {
        return "Name: Thin Test\r\n" +
               "Author: Don Corley\r\n" +
               "E-Mail: don@tourgeek.com";
    }
    /**
     * Add any applet sub-panel(s) now.
     */
    public boolean addSubPanels(Container parent)
    {
        parent.setLayout(new BoxLayout(parent, BoxLayout.X_AXIS));
        FieldList record = new TestTable(this);

//+     this.linkNewRemoteTable(null, record, true);
//------------------------
        Application app = this.getApplication();
        PhysicalDatabaseParent dbOwner = (PhysicalDatabaseParent)((ThinApplication)app).getPDatabaseParent(BaseApplet.mapDBParentProperties, true);
        PTable pTable = dbOwner.getPDatabase(record.getDatabaseName(), ThinPhysicalDatabase.NET_TYPE, true).getPTable(record, true, true);
//------------------------
        
        FieldTable table = record.getTable();
        try   {
            table.addNew();
//          record.getFieldInfo("ID").setString("1");
            record.getField("TestName").setString("no 1");
            table.add(record);
            table.addNew();
            record.getField("TestName").setString("no 2");
            table.add(record);
            table.close();
//          while (table.hasNext())
            {
//              table.next();
//System.out.println("Test/103--==================" + record.toString() + "==========");
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
//------------------------

        model = new TestGridModel(record.getTable());
        thinscreen = new JTable(model);
        model.setGridScreen(thinscreen, false);
            TableColumn newColumn = thinscreen.getColumnModel().getColumn(0);
            ImageIcon icon = (ImageIcon)model.getValueAt(0, 0);
            newColumn.setPreferredWidth(20);    // Yeah I know, but I know the width and I don't want to have to wait to load the icon.
            JCellButton button = new JCellButton(icon);
            newColumn.setCellEditor(button);
            button.addActionListener(this);
            button = new JCellButton(icon);
            newColumn.setCellRenderer(button);
        JScrollPane scrollpane = new JScrollPane(thinscreen);
        parent.add(scrollpane);
        return true;
    }
    /**
     *  The main() method acts as the applet's entry point when it is run
     *  as a standalone application. It is ignored if the applet is run from
     *  within an HTML page.
     */
    public static void main(String args[])
    {
        BaseApplet.main(args);
        TestThinGridApplet applet = (TestThinGridApplet)TestThinGridApplet.getSharedInstance();
        if (applet == null)
            applet = new TestThinGridApplet(args);
        new JBaseFrame("Test", applet);
    }
    public void actionPerformed(final java.awt.event.ActionEvent p1)
    {
        int iRow = thinscreen.getSelectedRow();
        FieldList fieldList = model.makeRowCurrent(iRow, false);
        if (fieldList != null)
        {
            FieldInfo fieldInfo = fieldList.getField(0);
            if (fieldInfo != null)
            {
                String strID = fieldInfo.toString();
                if ((strID != null) && (strID.length() > 0))
                {
                    Container parent = thinscreen.getParent();
                    parent.remove(thinscreen);
                        FieldTable table = model.getFieldTable();
                        table.getRecord().getField(0).setString(strID);
                        try   {
                            if (table.seek(null))
                            {
//                              this.fieldsToControls();
                            }
                        } catch (Exception ex)  {
                        }
                    JBaseScreen screen = new TestThinScreen(null, table.getRecord());
                    parent.add(screen);
                    
                    this.invalidate();
                    this.validate();
                }
                System.out.println("ID = " + strID);
            }
        }
    }
AbstractThinTableModel model = null;
JTable thinscreen = null;
}
