package org.jbundle.thin.app.test.manual.gridheader;

/**
 * OrderEntry.java:   Applet
 *  Copyright � 1997 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;

import org.jbundle.thin.app.test.vet.db.Cat;
import org.jbundle.thin.app.test.vet.db.Vet;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JScreen;
import org.jbundle.thin.base.screen.grid.JGridScreen;
import org.jbundle.thin.base.screen.grid.ThinTableModel;


/**
 * Main Class for applet OrderEntry
 */
public class TestThinVetScreen extends JScreen
{
    private static final long serialVersionUID = 1L;
    
    /**
     *  OrderEntry Class Constructor.
     */
    public TestThinVetScreen()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public TestThinVetScreen(Object parent, Object obj)
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
    public boolean addSubPanels(Container parent)
    {
        super.addSubPanels(parent);
        
        BaseApplet baseApplet = BaseApplet.getSharedInstance();
        RemoteSession remoteSession = baseApplet.makeRemoteSession(m_remoteSession, ".test.vet.remote.CatSession");
        Cat recCat = new Cat(this);
        baseApplet.linkRemoteSessionTable(remoteSession, recCat, false);

        JTestGridScreen gridScreen = new JTestGridScreen(parent, recCat);

        m_gbconstraints.weightx = 0.0;                        // Minimum width to hold labels
        m_gbconstraints.anchor = GridBagConstraints.NORTHWEST;    // Labels right justified
        m_gbconstraints.gridx = 0;                            // Column 0
        m_gbconstraints.gridy = GridBagConstraints.RELATIVE;  // Bump Row each time
        m_gbconstraints.gridheight = GridBagConstraints.REMAINDER; // end row
//        m_gbconstraints.anchor = GridBagConstraints.NORTH;
        m_gbconstraints.gridwidth = GridBagConstraints.REMAINDER; // end column
        
//        gridScreen.setOpaque(false);
        gridScreen.setName(Constants.GRID);
        GridBagLayout gridbag = (GridBagLayout)this.getScreenLayout();
        gridbag.setConstraints(gridScreen, m_gbconstraints);
        ((Container)parent).add(gridScreen);
//        gridScreen.addActionListener(this);     
        return true;
    }
    RemoteSession m_remoteSession = null;
    /**
     * Build the list of fields that make up the screen.
     */
    public FieldList buildFieldList()
    {
//        return new Vet(null);   // If overriding class didn't set this

        BaseApplet baseApplet = BaseApplet.getSharedInstance();
        m_remoteSession = baseApplet.makeRemoteSession(null, ".test.vet.remote.VetSession");
        FieldList fieldList = new Vet(null);
        this.addFieldList(fieldList);
        baseApplet.linkRemoteSessionTable(m_remoteSession, this.getFieldList(), false);

        return fieldList;
    }
    /**
     * When a control loses focus, move the field to the data area.
     * @param e The focus event.
     */
    public void focusLost(FocusEvent e)
    {
        boolean bFirstChange = false;
        if (this.getFieldList() != null)
            if (this.getFieldList().getEditMode() == Constants.EDIT_ADD)
                if (this.getFieldList().isModified() == false)
                    bFirstChange = true;
        super.focusLost(e);
        if (bFirstChange)
        {
            m_componentNextFocus = null;
            Component component = (Component)e.getSource();
            String string = component.getName();
            FieldInfo field = this.getFieldList().getField(string);        // Get the fieldInfo for this component
            if (field != null)
                if ("ID".equals(string))
                {
                    this.getFieldList().setKeyName("ID");
                    this.readKeyed(field);
                    
//&                    m_remoteSession.doRemoteAction(Constants.RESET, null);
                    JGridScreen gridScreen = (JGridScreen)this.getComponentByName(Constants.GRID);
                    ThinTableModel model = gridScreen.getGridModel();
                    FieldTable table = model.getFieldTable();
                    table.close();
                    model.resetTheModel();
                }
        }
    }
    /**
     * Process this action.
     */
    public boolean doAction(String strAction, int iOptions)
    {
        try   {
            if (Constants.RESET.equalsIgnoreCase(strAction))
            {
                m_remoteSession.doRemoteAction(strAction, null);    // Tell the remote session that I'm done with this booking
                    JGridScreen gridScreen = (JGridScreen)this.getComponentByName(Constants.GRID);
                    ThinTableModel model = gridScreen.getGridModel();
                    FieldTable table = model.getFieldTable();
                    table.close();
                    model.resetTheModel();
            }
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
        return super.doAction(strAction, iOptions);
    }
}
