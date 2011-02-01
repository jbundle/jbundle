package org.jbundle.thin.opt.location;

/**
 * OrderEntry.java:   Applet
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.JBaseScreen;


/**
 * Main Class for applet OrderEntry
 */
public class JLocationScreen extends JBaseScreen
    implements PropertyChangeListener
{
    private static final long serialVersionUID = 1L;
    
    /**
     *  OrderEntry Class Constructor.
     */
    public JLocationScreen()
    {
        super();
    }
    /**
     *  OrderEntry Class Constructor.
     */
    public JLocationScreen(Object parent, Object strLocationParam)
    {
        this();
        this.init(parent, strLocationParam);
    }
    /**
     * The init() method is called by the AWT when an applet is first loaded or
     * reloaded.  Override this method to perform whatever initialization your
     * applet needs, such as initializing data structures, loading images or
     * fonts, creating frame windows, setting the layout manager, or adding UI
     * components.
     */
    public void init(Object parent, Object strLocationParam)
    {
        super.init(parent, null);
        
        BaseApplet applet = (BaseApplet)parent;

        JTreePanel panelLeft = new JTreePanel(this, applet);
        if (strLocationParam instanceof String)
            panelLeft.setLocationParamName((String)strLocationParam);
        panelLeft.addPropertyChangeListener(this);      // So I can pass it down
        
        JPanel panelRight = new JMapPanel(this, applet);

        JSplitPane panel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelLeft, panelRight);
        panel.setContinuousLayout(true);
        panel.setPreferredSize(new Dimension(500, 400));
        panel.setOpaque(false);

        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(500, 300));
        this.add(panel, BorderLayout.CENTER);
    }
    /**
     * Pass the property changes on to my listeners.
     */
    public void propertyChange(final java.beans.PropertyChangeEvent p1) {
        this.firePropertyChange(p1.getPropertyName(), p1.getOldValue(), p1.getNewValue());
    }
}
