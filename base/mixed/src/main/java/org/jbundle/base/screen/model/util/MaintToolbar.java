/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.util;

/**
 * @(#)STEView.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.thin.base.db.Converter;


/**
 * Set up a tool bar control for a maint screen.
 */
public class MaintToolbar extends ToolScreen
{
    /**
     * Constructor.
     */
    public MaintToolbar()
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
    public MaintToolbar(ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc)
    {
        this();
        this.init(itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc);
    }
    /**
     * Controls for a maint screen.
     */
    public void setupMiddleSFields()
    {
        this.setupMaintSFields();
    }
}
