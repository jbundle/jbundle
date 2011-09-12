/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.screentest;

/**
 * OrderEntry.java:   Applet
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import org.jbundle.thin.app.test.test.db.TestTable;
import org.jbundle.thin.base.db.FieldList;
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
}
