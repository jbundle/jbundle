/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.swing;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.JTextComponent;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.thin.base.db.Constants;


/**
 * SEditText - A standard text input box.
 */
public class VEditText extends VScreenField
{

    /**
     * Constructor.
     */
    public VEditText()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VEditText(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public void init(ScreenField model, boolean bEditableControl)
    {
        super.init(model, bEditableControl);
    }
    /**
     * Free.
     */
    public void free()
    {
        if (this.getControl(DBConstants.CONTROL_TO_FREE) != null)
            if (m_bEditableControl)
        {
            this.getControl(DBConstants.CONTROL_TO_FREE).removeFocusListener(this);
            this.getControl(DBConstants.CONTROL_TO_FREE).removeKeyListener(this);
            m_bEditableControl = false;     // Don't remove more listeners in super
        }
        super.free();
    }
    /**
     * Create the physical control.
     * @param bEditableControl Is this control editable?
     * @return The new control.
     */
    public Component setupControl(boolean bEditableControl)
    {
        int cols = 10;
        if (this.getScreenField().getConverter() != null)
            cols = this.getScreenField().getConverter().getMaxLength();
        JTextComponent control = null;
        if (cols > ScreenConstants.kMaxSingleLineChars)
        {
            control = new JTextArea(2, ScreenConstants.kMaxSingleChars);
            control.setBorder(m_defaultBorder);
        }
        else if (cols > ScreenConstants.kMaxSingleChars)
            control = new JTextField(cols);   //? ScreenConstants.kMaxSingleChars
        else
            control = new JTextField(cols);

        if (bEditableControl)
        {
            control.addFocusListener(this);
            control.addKeyListener(this);
        }
        return control;
    }
    /**
     * Get the background color for this control.
     * @return The background color, or null if there is none.
     */
    public Color getControlBackgroundColor()
    {
        if (this.getScreenInfo() != null)
            if (!this.getScreenInfo().isCustomTheme()) // If there is a custom theme, the theme will set the fonts
                return this.getScreenInfo().getControlColor();
        return null;
    }
    /**
     * Set the the physical control color, font etc.
     * @param component The physical control.
     * @param bSelected Is it selected?
     * @param bIsInput This this an input (vs a display) field?
     * @param bGridControl Is it a grid control?
     */
    public void setControlAttributes(Component component, boolean bIsInput, boolean bSelected, boolean bGridControl)
    {
        super.setControlAttributes(component, bIsInput, bSelected, bGridControl);
    }
    /**
     *  This control lost focus (A Focus Listener).
     * @param evt The focus event.
     */
    public void focusLost(FocusEvent evt)
    {
        int iCaretPosition = 0;
        int iLength = 0;
        int iStart =  0;
        int iEnd =  0;
        Object data = null;
        BaseField field = null;

        JTextComponent textComponent = (JTextComponent)this.getControl();
        if (textComponent != null)
        {
            iCaretPosition = textComponent.getCaretPosition();
            iLength = textComponent.getDocument().getLength();
            iStart =  textComponent.getSelectionStart();
            iEnd =  textComponent.getSelectionEnd();
            if (this.getScreenField().getConverter() != null)
                field = (BaseField)this.getScreenField().getConverter().getField();
            if (field != null)
                data = field.getData();     // Data to undo
        }

        super.focusLost(evt);

        if (textComponent != null)
        {
            if (field != null) if (field.isJustModified())
                this.getScreenField().getRootScreen().getAppletScreen().undoTargetLost((ScreenField)this.getScreenField(), data);

            if (iLength > 0)
                if (iLength == ((JTextComponent)this.getControl()).getDocument().getLength())
            {
                if (iCaretPosition != textComponent.getCaretPosition())
                    if (iCaretPosition < iLength)
                        ((JTextComponent)this.getControl()).setCaretPosition(iCaretPosition); // Restore the caret
                if (iStart != iEnd)
                    if ((iStart != textComponent.getSelectionStart())
                        || (iEnd != textComponent.getSelectionEnd()))
                    {
                        textComponent.setSelectionStart(iStart);
                        textComponent.setSelectionEnd(iEnd);
                    }
            }
        }
    }
    /**
     * Key released, if tab, select next field.
     * @param evt The Key event.
     */
    public void keyPressed(KeyEvent evt)
    {
        if ((this.getControl() instanceof JTextArea)        // Text box
            || (this.getControl() instanceof JTextPane))    // HTML edit box
        {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER)
                || (evt.getKeyCode() == KeyEvent.VK_TAB))
            {
                boolean bRequestNextFocus = true;
                JTextComponent control = (JTextComponent)this.getControl();
                int iTextLength = control.getDocument().getLength();
                if (((control.getSelectionStart() == 0) || (control.getSelectionStart() == iTextLength))
                        && ((control.getSelectionEnd() == 0) || (control.getSelectionEnd() == iTextLength)))
                    bRequestNextFocus = true;
                else
                    bRequestNextFocus = false;      // Not fully selected, definitely process this key
                if (bRequestNextFocus)
                {
                    String strControl = control.getText();
                    String strData = null;
                    if (this.getScreenField().getConverter() != null)
                        strData = this.getScreenField().getConverter().getString();
                    if (!strControl.equals(strData))
                        if ((iTextLength != 0)
                            || (strData.length() != 0))
                        bRequestNextFocus = false;      // Data changed, definetly process this key
                }

                if (evt.getKeyCode() == KeyEvent.VK_ENTER)
                    if (!bRequestNextFocus)
                        return;     // Do not consume... process the return in the control
                if (evt.getKeyCode() == KeyEvent.VK_TAB)
                    if (bRequestNextFocus)
                {
                    int bPrevious = (evt.isShiftDown() ? DBConstants.SELECT_PREV_FIELD : DBConstants.SELECT_NEXT_FIELD);
                    this.getScreenField().selectField(this.getScreenField(), bPrevious);
                    evt.consume();	// Consume this key
                }
            }
        }
        super.keyPressed(evt);
    }
    /**
     * Get this component's current state in an object that can be used to set this
     * component's converter value using setConverterState(obj).
     * @param control The control to get the state from.
     * @return The control's value.
     */
    public Object getComponentState(Component control)
    {
        return ((JTextComponent)control).getText();
    }
    /**
     * Set the component to this state. State is defined by the component.
     * @param control The control to set the state to.
     * @param objValue The value to set the control to.
     */
    public void setComponentState(Component control, Object objValue)
    {
        if (objValue == null)
            objValue = Constants.BLANK;
        ((JTextComponent)control).setText((String)objValue);
    }
    /**
     * Give this control the input focus.
     * Currently, this method does nothing... it can be easily change to select all
     * when the focus is requested.
     * @return True if successful.
     */
    public void requestFocus()
    {
        super.requestFocus();
        /*  Uncomment these lines if you want to select all on tab.
        if (bSuccess)
            if (this.getScreenFieldView().getControl() != null)
                ((JTextComponent)this.getScreenFieldView().getControl()).selectAll();
        */
    }
}
