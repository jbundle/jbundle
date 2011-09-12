/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.cal.popup;

/**
 * JPopupPanel - Note this component is shared because it is used in the thin and thick calendars.
 *  Copyright � 2008 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.event.ActionListener;

import org.jbundle.thin.base.screen.BaseApplet;

/**
 * The popup panel to display on calendar clicks.
 * Note this component is shared because it is used in the thin and thick calendars.
 */
public class JPopupPanel extends JBasePopupPanel
{
    private static final long serialVersionUID = 1L;

    public JPopupPanel()
    {
        super();
    }
    public JPopupPanel(BaseApplet applet, ActionListener actionListener)
    {
        this();
        this.init(applet, actionListener);
    }
    public void init(BaseApplet applet, ActionListener actionListener)
    {
        super.init(applet, actionListener);
    }
    /**
     * Add all the components in this panel.
     * @param applet
     */
    public void addComponents(BaseApplet applet)
    {
        super.addComponents(applet);
        
        this.add(this.createComponentButton(ProductConstants.TOUR, applet));
        this.add(this.createComponentButton(ProductConstants.AIR, applet));
        this.add(this.createComponentButton(ProductConstants.HOTEL, applet));
        this.add(this.createComponentButton(ProductConstants.LAND, applet));
        this.add(this.createComponentButton(ProductConstants.TRANSPORTATION, applet));
        this.add(this.createComponentButton(ProductConstants.CAR, applet));
        this.add(this.createComponentButton(ProductConstants.CRUISE, applet));
        this.add(this.createComponentButton(ProductConstants.ITEM, applet));
    }
}
