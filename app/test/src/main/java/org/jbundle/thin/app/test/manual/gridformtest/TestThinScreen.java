/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.gridformtest;

/**
 * OrderEntry.java:   Applet
 *  Copyright � 1997 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import javax.swing.JOptionPane;

import org.jbundle.thin.app.test.test.db.TestTable;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.screen.JBaseScreen;
import org.jbundle.thin.base.screen.JScreen;


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
        this.addScreenButtons(this);
    }
    /**
     * Build the list of fields that make up the screen.
     */
    public FieldList buildFieldList()
    {
        return new TestTable(null);   // If overriding class didn't set this
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

    /**
     * Process this action.
     * @param strAction The action to process.
     * By default, this method handles RESET, SUBMIT, and DELETE.
     */
    public boolean doAction(String strAction, int iOptions)
    {
        if (Constants.RESET.equalsIgnoreCase(strAction))
        {
            this.setVisible(false);
            
            if (JOptionPane.showConfirmDialog(this.getBaseApplet(), "alert", "alert", JOptionPane.ERROR_MESSAGE) == 0)
            {
                
            }
            this.setVisible(true);
            this.resetFocus();
            return true;
        }
        return super.doAction(strAction, iOptions);
    }

}
