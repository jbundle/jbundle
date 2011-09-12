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
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.MissingResourceException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Popup;

import org.jbundle.model.Freeable;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.util.calendarpanel.PopupPanel;
import org.jbundle.thin.base.screen.menu.JUnderlinedButton;

/**
 * The popup panel to display on calendar clicks.
 * Note this component is shared because it is used in the thin and thick calendars.
 */
public class JBasePopupPanel extends JPanel
    implements Freeable, ActionListener, PopupPanel
{
    private static final long serialVersionUID = 1L;
    protected ActionListener m_actionListener = null;
    protected Popup popup = null;

    public JBasePopupPanel()
    {
        super();
    }
    public JBasePopupPanel(BaseApplet applet, ActionListener actionListener)
    {
        this();
        this.init(applet, actionListener);
    }
    public void init(BaseApplet applet, ActionListener actionListener)
    {
        m_actionListener = actionListener;
        this.addComponents(applet);
    }
    /**
     * Add all the components in this panel.
     * @param applet
     */
    public void addComponents(BaseApplet applet)
    {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        String strTitle = "Add component:";
        try {
            strTitle = applet.getApplication().getResources(null, true).getString(strTitle);
        } catch (MissingResourceException e) {  // Ignore
        }
        this.add(new JLabel(strTitle));
        //+this.add(this.createComponentButton(ProductConstants.TOUR, applet));
        this.setBackground(new Color(255, 255, 224));
    }
    /**
     * Create the button/panel for this menu item.
     * @param record The menu record.
     * @return The component to add to this panel.
     */
    public JComponent createComponentButton(String strProductType, BaseApplet applet)
    {
        ProductTypeInfo productType = ProductTypeInfo.getProductType(strProductType);
        JUnderlinedButton button = new JUnderlinedButton(productType.getDescription(), productType.getStartIcon());

        String strLink = strProductType;
        button.setName(strLink);
        
        button.setOpaque(false);
        Color colorBackground = productType.getSelectColor();
        button.setBackground(colorBackground); // Since the button is opaque, this is only needed for those look and feels that want their own background color.
        button.setBorderPainted(false);
        button.addActionListener(m_actionListener);
        button.addActionListener(this);
        return button;
    }
    /**
     * Free this object's resources.
     */
    public void free()
    {   // Cleanup - Remove the action listeners
        for (int i = 0; i < this.getComponentCount(); i++)
        {
            Component component = this.getComponent(i);
            if (component instanceof JButton)
            {
                ((JButton)component).removeActionListener(this);
                ((JButton)component).removeActionListener(m_actionListener);
            }
        }
    }
    /**
     * Set my popup parent, so I can hide when the user clicks a button.
     * @param popupParent
     */
    public void setPopupParent(Popup popupParent)
    {
        popup = popupParent;
    }
    /**
     * Any Action (such as click) means hide this component's popup (For now)!
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (popup != null)
            popup.hide();
    }
}
