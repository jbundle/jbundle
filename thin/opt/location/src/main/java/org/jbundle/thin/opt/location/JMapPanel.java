/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.opt.location;
/**
 * @(#)SampleTree.java  1.17 99/04/23
 */

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

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

public class JMapPanel extends JBaseScreen
{
    private static final long serialVersionUID = 1L;

    /**
     *  OrderEntry Class Constructor.
     */
    public JMapPanel()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public JMapPanel(Object parent, Object obj)
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

        BaseApplet applet = (BaseApplet)obj;
        JScrollPane sp = new JScrollPane();
        sp.setPreferredSize(new Dimension(300, 300));
        JLabel labelMap = new JLabel(applet.loadImageIcon("tour/maps/Continent", "Continent"));
        labelMap.setToolTipText("Sorry, the hotspot map is not operational yet");
        labelMap.setOpaque(false);
        sp.getViewport().add(labelMap);
        /** And show it. */
        this.setLayout(new BorderLayout());
        this.add("Center", sp);
    }
}
