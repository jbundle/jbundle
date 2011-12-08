/*
 * BaseAction.java
 *
 * Created on August 30, 2005, 10:48 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.jbundle.thin.base.screen.BaseApplet;


/**
 *
 * @author don
 */
public class BaseAction extends AbstractAction
{
	private static final long serialVersionUID = 1L;

	public static final String TIP = "Tip";

    protected ActionListener m_targetListener = null;
    
    protected String m_actionKey = null;

    /**
     * Creates a new instance of BaseAction
     */
    public BaseAction()
    {
        super();
    }
    /**
     * Creates a new instance of BaseAction
     */
    public BaseAction(String actionKey, ActionListener targetListener)
    {
        this();
        this.init(actionKey, targetListener);
    }
    /**
     * Creates a new instance of BaseAction.
     * @param actionKey The menu description key for this item.
     */
    public void init(String actionKey, ActionListener targetListener)
    {
        m_targetListener = targetListener;
        m_actionKey = actionKey;
        
        String text = BaseApplet.getSharedInstance().getString(actionKey);
        String desc = BaseApplet.getSharedInstance().getString(actionKey + TIP);
        ImageIcon icon = BaseApplet.getSharedInstance().loadImageIcon(actionKey);

        ActionManager.getActionManager().put(actionKey, this);
        
        this.putValue(AbstractAction.NAME, text);
        if (desc != null)
            if (!desc.equalsIgnoreCase(actionKey + TIP))
                this.putValue(AbstractAction.SHORT_DESCRIPTION, desc);
        if (icon != null)
            this.putValue(AbstractAction.SMALL_ICON, icon);
    }
    /**
     * Action performed.
     * If this is a standard action, this will work fine.
     * Otherwise, override this and do what you need to do.
     */
    public void actionPerformed(ActionEvent e)
    {
        if (m_targetListener != null)
            m_targetListener.actionPerformed(e);
    }
    
    /**
     * Get the action key.
     */
    public String getActionKey()
    {
        return m_actionKey;
    }

}
