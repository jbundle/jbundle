package org.jbundle.thin.app.test.manual.virtualmessage;

/**
 * OrderEntry.java:   Applet
 *  Copyright � 1997 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.rmi.RemoteException;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.db.client.RemoteFieldTable;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JScreen;
import org.jbundle.thin.base.screen.JScreenToolbar;


/**
 * Main Class for applet OrderEntry
 */
public class TestThinScreen extends JScreen
{
    private static final long serialVersionUID = 1L;
    
    /**
     *  OrderEntry Class Constructor.
     */
    public TestThinScreen()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public TestThinScreen(Object parent, Object obj)
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

        BaseMessageManager.createScreenMessageListener(this.getFieldList(), this);
    }
    /**
     * 
     */
    public void free()
    {
        BaseMessageManager.freeScreenMessageListeners(this);
        
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
//        return new TestTable(null);   // If overriding class didn't set this
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
     * Process this action.
     * This class calls controltofields on submit and resetfields on reset.
     * @param strAction The message or command to propagate.
     */
    public boolean doAction(String strAction, int iOptions)
    {
        if (strAction == Constants.RESET)
        {
            RemoteSession remoteSession = ((RemoteFieldTable)this.getFieldList().getTable()).getRemoteTableType(java.rmi.server.RemoteStub.class);
            try {
                remoteSession.doRemoteAction(strAction, null);
            } catch (Exception ex) {
                
            }
            return true;
        }
        return super.doAction(strAction, iOptions);
    }
    /**
     * When a control loses focus, move the field to the data area.
     */
    public void focusLost(FocusEvent e)
    {
        super.focusLost(e);
        m_componentNextFocus = null;
        Component component = (Component)e.getSource();
        if (component instanceof JTextComponent)
        {
            String string = component.getName();
            FieldInfo field = this.getFieldList().getField(string);        // Get the fieldInfo for this component
            if (field != null)
                if ("ID".equals(string))
                    this.readKeyed(field);
        }
    }
    /**
     * Add the toolbars?
     */
    public JComponent createToolbar()
    {
        return new JScreenToolbar(this, null);
    }
}
