/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.util;

/**
 * @(#)ToolScreen.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Map;

import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.thin.base.db.Converter;


/**
 * EmptyToolbar - A Toolbar that starts out with no buttons (you have to add them all).
 */
public class EmptyToolbar extends ToolScreen
{
    /**
     * Constructor.
     */
    public EmptyToolbar()
    {
        super();
    }
    /**
     * Constructor.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     */
    public EmptyToolbar(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Do not set up any buttons by default.
     */
    public void setupSFields()
    {
    }
}
