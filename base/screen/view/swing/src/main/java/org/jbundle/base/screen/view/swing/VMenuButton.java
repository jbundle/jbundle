/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.swing;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jbundle.base.model.DBConstants;
import org.jbundle.base.screen.model.SButtonBox;
import org.jbundle.base.screen.model.SMenuButton;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.thin.base.screen.menu.JUnderlinedButton;



/**
 * Implements a button to be used as an item in menus.
 */
public class VMenuButton extends VCannedBox
{
    public static Border DESC_BORDER = new EmptyBorder(0, 20, 0, 0);

    /**
     * Constructor.
     */
    public VMenuButton()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VMenuButton(ScreenField model,boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public void init(ScreenComponent model, boolean bEditableControl)
    {
        super.init(model, bEditableControl);
    }
    /**
     * Free.
     */
    public void free()
    {
        if (this.getControl() != null)
            if (m_bEditableControl)
        {
            JUnderlinedButton control = (JUnderlinedButton)this.getControl();
            control.removeFocusListener(this);
            control.removeActionListener(this);
            m_bEditableControl = false;     // Don't remove more listeners in super
        }
        super.free();
    }
    /**
     * Create the physical control.
     */
    public Component setupControl(boolean bEditableControl)
    {
//      This starting code is the same as VButtonBox, but I use JUnderlinedButtons instead of JButtons;
        String strDesc = ((SButtonBox)this.getScreenField()).getButtonDesc();
        JUnderlinedButton control = null;
        String strImageButton = ((SButtonBox)this.getScreenField()).getImageButtonName();
        if (strImageButton == null)
            control = new JUnderlinedButton(strDesc); // Physical control
        else if ((strDesc == null) || (strDesc.length() == 0))
            control = new JUnderlinedButton(this.loadImageIcon(strImageButton, null));  // Get this image, then redisplay me when you're done
        else
            control = new JUnderlinedButton(strDesc, this.loadImageIcon(strImageButton, null));   // Get this image, then redisplay me when you're done
        String strToolTip = ((SButtonBox)this.getScreenField()).getToolTip();
        if (strToolTip != null)
            control.setToolTipText(strToolTip);
        control.setMargin(NO_INSETS);
        if (bEditableControl)
        {
            control.addFocusListener(this);
            control.addActionListener(this);
        }
        control.setOpaque(false);

        control.setBorderPainted(false);
        
        String m_strDescText = ((SMenuButton)this.getScreenField()).getDescText();
        if ((m_strDescText != null) && (m_strDescText.length() > 0))
        {
            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setLayout(new BorderLayout());
            panel.add(control, BorderLayout.NORTH);
            if (m_strDescText.indexOf("<html>") != 0)
                if (this.getScreenInfo().isTextBoxStyle())
            {
                m_strDescText = "<html><body>" + m_strDescText + "</body></html>";                
            }
            if (m_strDescText.indexOf("<html>") == 0)
            {
                JLabel textArea = new JLabel(m_strDescText)
                {
                    private static final long serialVersionUID = 1L;

                    public Dimension getPreferredSize()
                    {
                        Dimension dim = super.getPreferredSize();
                        return new Dimension( Math.min(150, dim.width), Math.min(100, dim.height));
                    }
                };
                textArea.setOpaque(false);
                
                Font f = this.getScreenInfo().getFont("TextArea.font");
                if (f != null)
                    textArea.setFont(f);
                textArea.setBorder(DESC_BORDER);
                
                panel.add(textArea, BorderLayout.SOUTH);
            }
            else
            {
                JTextArea textArea = new JTextArea(m_strDescText)
                {
                    private static final long serialVersionUID = 1L;

                    public Dimension getPreferredSize()
                    {
                        Dimension dim = super.getPreferredSize();
                        return new Dimension(dim.width, Math.min(100, dim.height));
                    }
                };
                textArea.setOpaque(false);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setEditable(false);
                panel.add(textArea, BorderLayout.SOUTH);
            }
        }
        return control;
    }
    /**
     * Get one of the physical components associated with this SField.
     * @param int iLevel    CONTROL_TOP - Parent physical control; CONTROL_BOTTOM - Lowest child control
     * NOTE: This method is used for complex controls such as a scroll box, where this control must be
     * added to the parent, but sub-controls must be added to a lower level physical control.
     * @param iLevel The level for this control (top/bottom/etc).
     * @return The control for this view.
     */
    public Component getControl(int iLevel)
    {
        String strDescText = ((SMenuButton)this.getScreenField()).getDescText();
        if (iLevel == DBConstants.CONTROL_TOP)
            if ((strDescText != null) && (strDescText.length() > 0))
        {
            Container parent = this.getControl().getParent();
            while (!(parent instanceof JPanel))
            {
                parent = parent.getParent();
            }
            if (parent != null)
                return parent;  // scrollpane->JPanel
        }
        return super.getControl(iLevel);
    }
   /**
     * Set the the physical control color, font etc.
     * @param control The physical control.
     * @param bSelected Is it selected?
     * @param bIsInput This this an input (vs a display) field?
     * @param bGridControl Is it a grid control?
     */
    public void setControlAttributes(Object control, boolean bIsInput, boolean bSelected, boolean bGridControl)
    {
        super.setControlAttributes(control, bIsInput, bSelected, bGridControl);
        if (this.getScreenInfo() != null)
            if (control != null)
        {
            Color backgroundColor = this.getScreenInfo().getBackgroundColor();
            if (UIManager.getLookAndFeel() instanceof MetalLookAndFeel)
                backgroundColor = Color.gray;   // HACK! (For the metal L and F, if the button color is the same as the background, I get a gradient button)
            if (backgroundColor != null)
                ((Component)control).setBackground(backgroundColor);
        }
    }
}
