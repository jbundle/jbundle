package org.jbundle.thin.app.test.manual.sharedtest;

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
import java.awt.event.FocusEvent;
import java.rmi.RemoteException;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jbundle.thin.app.test.test.db.TestTable;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.remote.RemoteTable;
import org.jbundle.thin.base.remote.RemoteTask;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseScreen;
import org.jbundle.thin.base.screen.JScreen;
import org.jbundle.thin.base.screen.grid.JCellRemoteComboBox;
import org.jbundle.thin.base.screen.util.JFSCheckBox;
import org.jbundle.thin.base.screen.util.JFSImage;
import org.jbundle.thin.base.screen.util.JFSTextScroller;


/**
 * Main Class for applet OrderEntry
 */
public class TestFancyThinScreen extends JScreen
{
    private static final long serialVersionUID = 1L;
    
    /**
     *  OrderEntry Class Constructor.
     */
    public TestFancyThinScreen()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public TestFancyThinScreen(Object parent, Object obj)
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
    }
    protected int m_iColumnNumber = 1;
    /**
     * Add any screen sub-panel(s) now.
     * You might want to override this to create an alternate parent screen.
     * @param parent The parent to add the new screen to.
     */
    public boolean addSubPanels(Container parent)
    {
        parent.setLayout(new BoxLayout(parent, BoxLayout.X_AXIS));

        m_iColumnNumber = 1;    // Left
        JPanel panelLeftTop = new JPanel();
        panelLeftTop.setOpaque(false);
        panelLeftTop.setAlignmentY(JLabel.TOP_ALIGNMENT);
        parent.add(panelLeftTop);

        JPanel panelLeft = new JPanel();
        panelLeft.setOpaque(false);
        panelLeftTop.add(panelLeft);        
        super.addSubPanels(panelLeft);
        
        m_iColumnNumber = 2;    // Right
        JPanel panelRightTop = new JPanel();
        panelRightTop.setOpaque(false);
        panelRightTop.setAlignmentY(JLabel.TOP_ALIGNMENT);
        parent.add(panelRightTop);

        JPanel panelRight = new JPanel();
        panelRight.setOpaque(false);
        panelRightTop.add(panelRight);
        super.addSubPanels(panelRight);
        return true;
    }
    /**
     * Build the list of fields that make up the screen.
     */
    public FieldList buildFieldList()
    {
        return new TestTable(null);   // If overriding class didn't set this
    }
    /**
     * Get this field (or return null if this field doesn't belong on the screen).
     * This is the method to use to filter the items to display on the screen.
     * @param The index of this field in the record.
     * @return The fieldinfo object.
     */
    protected int m_iIDLocation = 100;
    protected int m_iMemoLocation = 100;
    protected int m_iImageLocation = 100;
    public Converter getFieldForScreen(int iIndex)
    {
        iIndex++;    // Skip ID

        FieldInfo fieldInfo = this.getFieldList().getField(iIndex);
        if (fieldInfo == null)
            return null;
        if ("TestMemo".equalsIgnoreCase(fieldInfo.getFieldName()))
            m_iMemoLocation = iIndex;
        if ("TestImage".equalsIgnoreCase(fieldInfo.getFieldName()))
            m_iImageLocation = iIndex;

        if ((iIndex >= m_iMemoLocation) && (iIndex < m_iImageLocation))
            iIndex++;
        else if (iIndex == m_iImageLocation)
            iIndex = m_iMemoLocation;
        fieldInfo = this.getFieldList().getField(iIndex);
        return fieldInfo;
    }
    /**
     * Add this label to the first column of the grid.
     * @param parent The container to add the control(s) to.
     * @param fieldInfo The field to add a label to.
     * @param gridbag The screen layout.
     * @param c The constraint to use.
     */
    public JComponent addScreenLabel(Container parent, Converter fieldInfo)
    {
//      if (this.getColumn(fieldInfo) == 2)
//          c.gridx = 2;                            // Column 1
        if (this.getColumn(fieldInfo) != m_iColumnNumber)
            return null;                         // Column 1
        return super.addScreenLabel(parent, fieldInfo);
    }
    /**
     * Add the screen controls to the second column of the grid.
     * @param parent The container to add the control(s) to.
     * @param fieldInfo the field to add a control for.
     * @param gridbag The screen layout.
     * @param c The constraint to use.
     */
    public JComponent addScreenControl(Container parent, Converter fieldInfo)
    {
//      if (this.getColumn(fieldInfo) == 2)
//          c.gridx = 3;                            // Column 1
        if (this.getColumn(fieldInfo) != m_iColumnNumber)
            return null;                         // Column 1
        GridBagConstraints c = this.getGBConstraints();
        c.gridwidth = 1; // end column
        return super.addScreenControl(parent, fieldInfo);
    }
    /**
     * Is this field in the second column (2 = second, 1 = first)?
     * @return true if it is.
     */
    public int getColumn(Converter fieldInfo)
    {
        for (int i = 0; i < this.getFieldList().getFieldCount(); i++)
        {
            if (this.getFieldForScreen(i) == null)
                continue;
            if ("TestDouble".equalsIgnoreCase(this.getFieldForScreen(i).getFieldName()))
                return 2; // Second column
            if (fieldInfo == this.getFieldForScreen(i))
                return 1; // First column
        }
        return 1;
    }
    /**
     * Add the screen controls to the second column of the grid.
     * Create a default component for this fieldInfo.
     * @param fieldInfo the field to create a control for.
     * @return The component.
     */
    public JComponent createScreenComponent(Converter fieldInfo)
    {
        JComponent component = null;
        if ("TestYesNo".equalsIgnoreCase(fieldInfo.getFieldName()))
            component = new JFSCheckBox(null);
        if ("TestDate".equalsIgnoreCase(fieldInfo.getFieldName()))
            component = new org.jbundle.thin.base.screen.util.cal.JCalendarDualField(fieldInfo);
        if ("TestDateTime".equalsIgnoreCase(fieldInfo.getFieldName()))
            component = new org.jbundle.thin.base.screen.util.cal.JCalendarDualField(fieldInfo);
        if ("TestImage".equalsIgnoreCase(fieldInfo.getFieldName()))
            component = new JFSImage(null);
        if ("TestMemo".equalsIgnoreCase(fieldInfo.getFieldName()))
            component = new JFSTextScroller(null);
        if ("TestLong".equalsIgnoreCase(fieldInfo.getFieldName()))
        {
            BaseApplet applet = BaseApplet.getSharedInstance();
            FieldList record = new org.jbundle.thin.app.test.vet.db.Vet(this);

            RemoteTable remoteTable = null;
            try   {
                synchronized (applet.getRemoteTask())
                {   // In case this is called from another task
                    RemoteTask server = (RemoteTask)applet.getRemoteTask();
                    Map<String, Object> dbProperties = applet.getApplication().getProperties();
                    remoteTable = server.makeRemoteTable(record.getRemoteClassName(), null, null, dbProperties);
                }
            } catch (RemoteException ex)    {
                ex.printStackTrace();
            } catch (Exception ex)  {
                ex.printStackTrace();
            }
            RemoteSession remoteSession = remoteTable;

            String strDesc = "Vet";
            String strFieldName = "Name";
            String strComponentName = "TestLong";
            boolean bCacheTable = true;
            String strIndexValue = "ID";
            return new JCellRemoteComboBox(applet, remoteSession, record, strDesc, strFieldName, strComponentName, bCacheTable, strIndexValue, null);
        }
        if (component == null)
            component = super.createScreenComponent(fieldInfo);
        return component;
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
                if ("TestCode".equals(string))
                {
                    this.getFieldList().setKeyName("TestCode");
                    this.readKeyed(field);
                }
        }
    }
    /**
     * Create a grid screen for this form.
     * @param record the (optional) record for this screen.
     * @return The new grid screen.
     */
    public JBaseScreen createGridScreen(FieldList record)
    {
        return new JTestGridScreen(this.getParentObject(), record);
    }

}
