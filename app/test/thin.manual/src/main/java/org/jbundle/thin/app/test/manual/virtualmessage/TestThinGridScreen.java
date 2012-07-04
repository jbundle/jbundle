/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.virtualmessage;

/**
 * JScreen.java:    Applet
 *  Copyright � 1997 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */


import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.remote.RemoteException;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.screen.AbstractThinTableModel;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.grid.JGridScreen;
import org.jbundle.thin.base.util.message.ThinMessageManager;


/**
 * A Basic data entry screen.
 * This screen is made of a panel with a GridBagLayout. Labels in the first column, aligned right.
 * Data fields in the second column aligned left.
 */
public class TestThinGridScreen extends JGridScreen
{
    private static final long serialVersionUID = 1L;

    protected RemoteSession m_remoteSession = null;

    /**
     *  JScreen Class Constructor.
     */
    public TestThinGridScreen()
    {
        super();
    }
    /**
     *  JScreen Class Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param record and the record or GridTableModel as the parent.
     */
    public TestThinGridScreen(Object parent, Object record)
    {
        this();
        this.init(parent, record);
    }
    /**
     * Initialize this class.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param record and the record or GridTableModel as the parent.
     */
    public void init(Object parent, Object record)
    {
        super.init(parent, record);
        
        ThinMessageManager.createScreenMessageListener(this.getFieldList(), this);
    }
    /**
     * 
     */
    public void free()
    {
        ThinMessageManager.freeScreenMessageListeners(this);
        
        super.free();
    }
    /**
     * Build the list of fields that make up the screen.
     * Override this to create a new record.
     * @return The fieldlist for this screen.
     */
    public FieldList buildFieldList()
    {
        try {
//        FieldList record = new TestTable(this);
            FieldList record = null;
            RemoteSession remoteSession = this.getBaseApplet().makeRemoteSession(null, ".test.manual.optcode.thinmessage.virtual.TestGridSession");
            RemoteTable remoteTable = remoteSession.getRemoteTable("TestTable");
            record = remoteTable.makeFieldList(null);
//            this.getBaseApplet().linkRemoteSessionTable(remoteSession, record, true);
            remoteTable = new org.jbundle.thin.base.db.client.CachedRemoteTable(remoteTable);
            BaseApplet applet = (BaseApplet)this.getTargetScreen(this, BaseApplet.class);
            FieldTable table = new org.jbundle.thin.base.db.client.RemoteFieldTable(record, remoteTable, applet);

            record.setTable(table);
            table.setRecord(record);
            return record;
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    /**
     * Build the list of fields that make up the screen.
     * Override this to create a new record.
     * @return The fieldlist for this screen.
     */
    public AbstractThinTableModel createGridModel(FieldList record)
    {
        return new TestGridModel(record.getTable());
//        return new ThinTableModel(record.getFieldTable());
    }
    /**
     * Process this action.
     * This class calls controltofields on submit and resetfields on reset.
     * @param strAction The message or command to propagate.
     */
    public boolean doAction(String strAction, int iOptions)
    {
        if (strAction == Constants.RESET)
        {
            RemoteSession remoteSession = m_remoteSession;
            try {
                remoteSession.doRemoteAction(strAction, null);
            } catch (Exception ex) {
                
            }
            return true;
        }
        return super.doAction(strAction, iOptions);
    }
}
