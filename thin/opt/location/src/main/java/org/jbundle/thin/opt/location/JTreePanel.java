/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.opt.location;
/**
 * @(#)SampleTree.java  1.17 99/04/23
 */

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jbundle.thin.base.remote.RemoteSession;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseScreen;


/**
  * A demo for illustrating how to do different things with JTree.
  * The data that this displays is rather boring, that is each node will
  * have 7 children that have random names based on the fonts.  Each node
  * is then drawn with that font and in a different color.
  * While the data isn't interesting the example illustrates a number
  * of things:
  *
  * For an example of dynamicaly loading children refer to DynamicTreeNode.
  * For an example of adding/removing/inserting/reloading refer to the inner
  *     classes of this class, AddAction, RemovAction, InsertAction and
  *     ReloadAction.
  * For an example of creating your own cell renderer refer to
  *     SampleTreeCellRenderer.
  * For an example of subclassing JTreeModel for editing refer to
  *     SampleTreeModel.
  *
  * @version 1.0.0
  * @author Scott Violet
  */

public class JTreePanel extends JBaseScreen
{
    private static final long serialVersionUID = 1L;

    public static String LOCATION_RECORD = "locationRecord";
    public static String LOCATION_ID = "locationID";
    public static String LOCATION = "location";
    
    protected String m_strLocationRecordParam = LOCATION_RECORD;
    protected String m_strLocationIDParam = LOCATION_ID;
    protected String m_strLocationParam = LOCATION;

    public static String CONTINENT = "Continent";
    public static String ROOT = "root";

    /** Tree used for the example. */
    protected JTree m_tree;
    /** Tree model. */
    protected DefaultTreeModel m_treeModel;
    
    /**
     *  OrderEntry Class Constructor.
     */
    public JTreePanel()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public JTreePanel(Object parent, Object obj)
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
        /** Create the JTreeModel. */
        BaseApplet applet = (BaseApplet)obj;
        RemoteSession parentSessionObject = null;//?this.getTourCalendarScreen().getRemoteSession();
        RemoteSession remoteSession = applet.makeRemoteSession(parentSessionObject, "com.tourapp.tour.product.remote.location.LocationSearchSession");

        String strListName = CONTINENT;
        String strRecordName = ROOT;
        DefaultMutableTreeNode root = new DynamicTreeNode(new NodeData(applet, remoteSession, strListName, null, strRecordName));
        m_treeModel = new DefaultTreeModel(root);

        /** Create the tree. */
        m_tree = new JTree(m_treeModel);
        m_tree.setOpaque(false);
        MouseListener ml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selRow = m_tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = m_tree.getPathForLocation(e.getX(), e.getY());
                if(selRow != -1) {
                    if(e.getClickCount() == 1) {
                        mySingleClick(selRow, selPath);
                    }
                    else if(e.getClickCount() == 2) {
                        myDoubleClick(selRow, selPath);
                    }
                }
            }
        };
        m_tree.addMouseListener(ml);

        m_tree.setCellRenderer(new TreeNodeCellRenderer());
        /** Put the Tree in a scroller. */
        JScrollPane sp = new JScrollPane();
        sp.setPreferredSize(new Dimension(300, 300));
        sp.getViewport().add(m_tree);
        
        /** And show it. */
        this.setLayout(new BorderLayout());
        this.add(BorderLayout.CENTER, sp);
    }
    /**
     * User selected item.
     */
    public NodeData mySingleClick(int selRow, TreePath selPath)
    {
        Object[] x = selPath.getPath();
        DynamicTreeNode nodeCurrent = (DynamicTreeNode)x[x.length - 1];     // Last one in list
        NodeData data = (NodeData)nodeCurrent.getUserObject();
        String strDescription = data.toString();
        String strID = data.getID();
        String strRecord = data.getRecordName();
        this.firePropertyChange(m_strLocationRecordParam, null, strRecord);
        this.firePropertyChange(m_strLocationIDParam, null, strID);
        this.firePropertyChange(m_strLocationParam, null, strDescription);
        return data;
    }
    /**
     *
     */
    public NodeData myDoubleClick(int selRow, TreePath selPath)
    { // If node is a leaf node, change property and close window
        NodeData data = this.mySingleClick(selRow, selPath);    // Make sure it's selected
        if (data.isLeaf())
        {
            BaseApplet applet = null;
            Container parent = this;
            while ((parent = parent.getParent()) != null)
            {
                if (parent instanceof BaseApplet)
                    applet = (BaseApplet)parent;
                if (parent instanceof JFrame)
                {
                    if (applet != null)
                        applet.free();
                    ((JFrame)parent).dispose();
                    break;
                }
            }
        }
        return data;
    }
    /**
     * Set the param name(s).
     */
    public void setLocationParamName(String strParam)
    {
        if ((strParam == null) || (strParam.length() == 0))
            strParam = LOCATION;
        m_strLocationRecordParam = strParam + "Record";
        m_strLocationIDParam = strParam + "ID";
        m_strLocationParam = strParam;
    }
}
