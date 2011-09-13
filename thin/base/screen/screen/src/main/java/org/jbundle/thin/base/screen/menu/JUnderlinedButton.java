/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.menu;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicGraphicsUtils;


/**
 * An JUnderlinedButton is a regular JButton that underlines the
 * text portion in blue on a mouseover.
 * Makes it look like a hyperlink.
 */
public class JUnderlinedButton extends JButton
    implements MouseListener
{
	private static final long serialVersionUID = 1L;

	/**
     * Turn on to underline this button.
     */
    protected boolean m_bUnderlined = false;
    
    /**
     * Constructor.
     */
    public JUnderlinedButton()
    {
        super();
    }
    /**
     * Constructor.
     * @param icon The button icon.
     * @param text The button text.
     */
    public JUnderlinedButton(String text, Icon icon)
    {
        this();
        this.init(icon, text);
    }
    /**
     * Constructor.
     * @param icon The button icon.
     */
    public JUnderlinedButton(Icon icon)
    {
        this();
        this.init(icon, null);
    }
    /**
     * Constructor.
     * @param text The button text.
     */
    public JUnderlinedButton(String text)
    {
        this();
        this.init(null, text);
    }
    /**
     * Constructor.
     * NOTE: The params are reversed, because JButton has an init(text, icon) method.
     * @param icon The (optional) button icon.
     * @param text The (optional) button text.
     */
    public void init(Icon icon, String text)
    {
        if (text != null)
            this.setText(text);
        if (icon != null)
            this.setIcon(icon);
        this.addMouseListener(this);
    }
    /**
     * Free this component.
     * Actually, just remove the listener.
     */
    public void free()
    {
        this.removeMouseListener(this);
    }
    /* These rectangles/insets are allocated once for all 
     * ButtonUI.paint() calls.  Re-using rectangles rather than 
     * allocating them in each paint call substantially reduced the time
     * it took paint to run.  Obviously, this method can't be re-entered.
     */
    private static Rectangle viewRect = new Rectangle();
    private static Rectangle textRect = new Rectangle();
    private static Rectangle iconRect = new Rectangle();

    /**
     * Paint.
     * NOTE: This is just like paint in BasicButtonUI, except for all the fancy L&F stuff.
     */
    public void paint(Graphics g) 
    {
        FontMetrics fm = this.getFontMetrics(this.getFont());

        Insets i = this.getInsets();

        viewRect.x = i.left;
        viewRect.y = i.top;
        viewRect.width = this.getWidth() - (i.right + viewRect.x);
        viewRect.height = this.getHeight() - (i.bottom + viewRect.y);

        textRect.x = textRect.y = textRect.width = textRect.height = 0;
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

        Font f = this.getFont();
        g.setFont(f);

        // layout the text and icon
        String text = SwingUtilities.layoutCompoundLabel(
            this, fm, this.getText(), this.getIcon(), 
            this.getVerticalAlignment(), this.getHorizontalAlignment(),
            this.getVerticalTextPosition(), this.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, 
	    this.getText() == null ? 0 : this.getIconTextGap());

        // Paint the Icon
        if(this.getIcon() != null) { 
            this.getIcon().paintIcon(this, g, iconRect.x, iconRect.y);;
        }

        if (text != null && !text.equals(""))
        {
            g.setColor(this.getForeground());
            int mnemonicIndex = this.getDisplayedMnemonicIndex();
            BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, mnemonicIndex, textRect.x, textRect.y + fm.getAscent());

            if (m_bUnderlined)
            {   // Draw the underline if underlined
                Color oldColor = g.getColor();
                g.setColor(Color.blue);
                g.drawLine(textRect.x, textRect.y + fm.getAscent(), textRect.x + textRect.width, textRect.y + fm.getAscent());
                g.setColor(oldColor);
            }
        }
    }

    /**
     * This item has been selected. Change the view.
     * @param bUnderlined Set this button to underlined.
     */
    public void setUnderlined(boolean bUnderlined)
    {
        m_bUnderlined = bUnderlined;
        this.repaint();
    }
    /**
     * Invoked when the mouse has been clicked on a component.
     * @param e The mouse event.
     */
    public void mouseClicked(MouseEvent e)
    {
    }
    /**
     * Invoked when the mouse enters a component.
     * @param e The mouse event.
     */
    public void mouseEntered(MouseEvent e)
    {
        this.setUnderlined(true);
    }
    /**
     * Invoked when the mouse exits a component.
     * @param e The mouse event.
     */
    public void mouseExited(MouseEvent e)
    {
        this.setUnderlined(false);
    }
    /**
     * Invoked when a mouse button has been pressed on a component.
     * @param e The mouse event.
     */
    public void mousePressed(MouseEvent e)
    {
    }
    /**
     * Invoked when a mouse button has been released on a component.
     * @param e The mouse event.
     */
    public void mouseReleased(MouseEvent e)
    {
    }
}
