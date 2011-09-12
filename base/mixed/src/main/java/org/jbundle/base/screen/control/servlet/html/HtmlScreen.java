/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.control.servlet.html;

/**
 * @(#)HtmlScreen.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.screen.model.TopScreen;
import org.jbundle.base.screen.view.ViewFactory;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.thin.base.db.FieldList;


/**
 * HtmlScreen - Set up Application Screen.
 * <p>Contains the main screen, toolbars, status bar(s), etc.
 */
public class HtmlScreen extends TopScreen
{

    /**
     * Constructor.
     */
    public HtmlScreen()
    {
        super();
    }
    /**
     * Initialize the RecordOwner.
     * This initializer is required by the RecordOwner interface.
     */
    public HtmlScreen(RecordOwnerParent parent, FieldList recordMain, Object properties)
    {
        this();
        this.init(parent, recordMain, properties);
    }
    /**
     * Free the resources.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get the environment to use for this record owner.
     * @return Record owner's environment, or null to use the default enviroment.
     */
    public ViewFactory getViewFactory()
    {
        if (m_viewFactory != null)
            return m_viewFactory;
        return m_viewFactory = ViewFactory.getViewFactory(HTML_TYPE, 'H');
    }
}
