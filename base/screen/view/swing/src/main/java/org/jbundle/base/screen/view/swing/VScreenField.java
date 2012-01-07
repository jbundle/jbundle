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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.screen.control.swing.util.ScreenInfo;
import org.jbundle.base.screen.model.BaseGridScreen;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.GridScreen;
import org.jbundle.base.screen.model.SButtonBox;
import org.jbundle.base.screen.model.SEditText;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.ToolScreen;
import org.jbundle.base.screen.model.util.Resources;
import org.jbundle.base.screen.view.ScreenFieldView;
import org.jbundle.base.screen.view.ScreenFieldViewAdapter;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.ResourceConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.model.Task;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.screen.landf.ScreenUtil;


/**
 * ScreenFieldView - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 */
public abstract class VScreenField extends ScreenFieldViewAdapter
    implements ScreenFieldView, ActionListener, FocusListener, TableCellRenderer, TableCellEditor, KeyListener, MouseListener
{
    /**
     * The Physical control.
     */
    protected Component m_control = null;
    /**
     * The Physical location.
     */
    protected Rectangle m_rectExtent = null;
    /**
     * Zero insets constants.
     */
    public final static Insets NO_INSETS = new Insets(0,0,0,0);
    /**
     * To time a mouse-double click.
     */
    protected long m_lMouseLastClick = 0;
    /**
     * 5/10 of a sec.
     */
    public static final int kiDoubleClickTimems = 500;
    /**
     * These are used for screenfields rendered and edited in a GridScreen.
     */
    protected JComponent m_componentRender = null;
    /**
     * These are used for screenfields rendered and edited in a GridScreen.
     */
    protected JComponent m_componentEditor = null;
    /**
     * These are used for screenfields rendered and edited in a GridScreen.
     */
    protected EventListenerList listenerList = null;
    /**
     * These are used for screenfields rendered and edited in a GridScreen.
     */
    transient protected ChangeEvent changeEvent = null;
    /**
     * A border used to convey focus.
     */
    protected static final Border m_focusBorder = new LineBorder(Color.black);
    /**
     * A border used to convey focus.
     */
    protected static final Border m_defaultBorder = new LineBorder(Color.gray);

    /**
     * Constructor.
     */
    public VScreenField()
    {
        super();
    }
    /**
     * Constructor.
     * @param model The model object for this view object.
     * @param bEditableControl Is this control editable?
     */
    public VScreenField(ScreenField model, boolean bEditableControl)
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
        m_rectExtent = null;

        m_componentRender = null;
        m_componentEditor = null;

        m_control = this.setupControl(bEditableControl);
    }
    /**
     * Free.
     */
    public void free()
    {
        if (m_control != null)
        {
            if (this.getControl(DBConstants.CONTROL_TOP) != null)
                if (this.getControl(DBConstants.CONTROL_TOP).getParent() != null)
            {
                this.getControl(DBConstants.CONTROL_TOP).getParent().remove(this.getControl(DBConstants.CONTROL_TOP));
                m_control = null;       // This keeps m_control from re-deleting this in OnDestroy!
            }
        }
        m_componentEditor = null;
        m_componentRender = null;
        super.free();
    }
    /**
     * Set the physical control.
     * @param control The physical control.
     */
    public void setControl(Component control)
    {
        m_control = control;
    }
    /**
     * Get the physical component associated with this SField.
     * @return The physical control.
     */
    public Component getControl()
    {
        return m_control;
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
        if (iLevel == DBConstants.CONTROL_TO_FREE)
            if (m_componentEditor != null)   // This is a special call to deal with the release of the listeners on a grid control.
                return m_componentEditor;
        return this.getControl();
    }
    /**
     * Set up the physical control (that implements Component).
     * @param bEditableControl Is this control editable?
     * @return The physical control.
     */
    public abstract Component setupControl(boolean bEditableControl); // Must o/r
    /**
     * Set this control's dimensions.
     * @param rect The control's bounds.
     */
    public void setControlExtent(Rectangle rect)
    {
        m_rectExtent = rect;
    } // Current size of the control
    /**
     * Get the current size of the control.
     * @return The control's bounds.
     */
    public Rectangle getControlExtent()
    {
        return m_rectExtent;
    }
    /**
     * Calc the control size.
     * @param ptLocation The location of this control.
     * @return The bounds for this control.
     */
    public Rectangle calcBoxShape(Point ptLocation)
    {
        short boxChars = 10;
        short boxLines = 1;
        if (this.getScreenField().getConverter() != null)
            boxChars = (short)this.getScreenField().getConverter().getMaxLength();
        if (boxChars > ScreenConstants.kMaxEditLineChars)
        {       // This should depend on the m_ScreenSize relative values
            boxChars = ScreenConstants.kMaxTEChars;
            boxLines = ScreenConstants.kMaxTELines;
        }
        else if (boxChars > ScreenConstants.kMaxSingleLineChars)
        {       // not int enough for a scroll box, but not single line
            if (boxChars > ScreenConstants.kMaxDoubleLineChars)
                boxLines = ScreenConstants.kMaxDoubleLines;
            else
                boxLines = ScreenConstants.kMaxSingleLines;
            boxChars = ScreenConstants.kMaxSingleChars;
        }
        else if (boxChars > ScreenConstants.kMaxSingleChars)
        {       // not enough for a few lines, but don't make a long field
            boxChars = ScreenConstants.kMaxSingleChars;
        }
        Dimension itsSize = this.getTextBoxSize(boxChars, ScreenConstants.NEXT_LOGICAL, boxLines);
        return new Rectangle(ptLocation.x , ptLocation.y, itsSize.width, itsSize.height);
    }
    /**
     * Calc the control size for this many rows and columns.
     * @param itsMaxChars The columns in this text box.
     * @param position The position.
     * @param itsMaxLines The rows for this text box.
     * @return The bounds for this text control.
     */   
    public Dimension getTextBoxSize(int itsMaxChars, int position, int itsMaxLines)
    {
        Dimension boxSize = new Dimension(0, 0);
        if (position == ScreenConstants.FIELD_DESC)
            itsMaxChars = ScreenConstants.kMaxDescLength;
        if (position == ScreenConstants.CHECK_BOX_DESC)
//            itsMaxChars = ScreenConstants.kMaxDescLength + 2;
            itsMaxChars = itsMaxChars + 4;
        if (position == ScreenConstants.POPUP_DESC)
            itsMaxChars += 2;
        boxSize.width = this.getScreenInfo().getBoxWidth(itsMaxChars);
        if (position != ScreenConstants.FIELD_DESC)     // Descriptions have to line up on the right
            boxSize.width += ScreenConstants.kExtraColBoxSpacing;

        if (itsMaxLines > 1)
            itsMaxLines++;  // Space for scroll bars
        boxSize.height = ((VScreenField)this.getScreenField().getBasePanel().getScreenFieldView()).getScreenInfo().getBoxHeight(this.getScreenField().getBasePanel(), itsMaxLines);
        if (position == ScreenConstants.POPUP_DESC)     // I added this for my Win95 version (Smaller fonts)
            boxSize.height += 2;
        return boxSize;
    }
    /**
     * Dimension of this text in current dc.
     * @param tempString The text to get the extent for.
     * @return The bounds for this text.
     */   
    public Dimension getTextExtent(String tempString)
    {
        Dimension textSize = null;
        FontMetrics fm = ((VScreenField)this.getScreenField().getBasePanel().getScreenFieldView()).getScreenInfo().getFontMetrics();
        if (fm != null)
            textSize = new Dimension(fm.stringWidth(tempString), fm.getHeight());
        else
            textSize = new Dimension(((VScreenField)this.getScreenField().getParentScreen().getScreenFieldView()).getScreenInfo().getBoxWidth(tempString.length()), ((VScreenField)this.getScreenField().getBasePanel().getScreenFieldView()).getScreenInfo().getBoxHeight(this.getScreenField().getBasePanel(), 1));
        return textSize;
    }
    /**
     * Set the the physical control color, font etc.
     * @param control The physical control.
     * @param bSelected Is it selected?
     * @param bIsInput This this an input (vs a display) field?
     * @param bGridControl Is it a grid control?
     */
    public void setControlAttributes(Component control, boolean bIsInput, boolean bSelected, boolean bGridControl)
    {
        if (control != null)
        {
            ScreenInfo screenInfo = this.getScreenInfo();
            if (screenInfo != null)
            {
                if (!bSelected)
                {   // Normal - not selected
                    Color colorText = null;
                    if (!screenInfo.isCustomTheme()) // If there is a custom theme, the theme will set the colors
                        colorText = screenInfo.getTextColor();
                    Color colorBackground = this.getControlBackgroundColor();
                    if (colorBackground == null)
                        if (bGridControl)
                            if (control != null)
                    {
                        if (m_colorNormal == null)
                            m_colorNormal = control.getBackground();
                        colorBackground = m_colorNormal;
                    }
                    if (colorText != null)
                        control.setForeground(colorText);
                    if (colorBackground != null)
                        control.setBackground(colorBackground);
                    if (!screenInfo.isCustomTheme())
                        if (control instanceof JTextComponent)
                            if (((JTextComponent)control).getDisabledTextColor().getRGB() == -4665371)
                                ((JTextComponent)control).setDisabledTextColor(Color.GRAY); // HACK, can't see disabled fields in Metal
                }
                else
                {   // Selected
                    if (m_colorSelected == null)
                    {       // First time
                        if (bGridControl)
                            if (this.getScreenField().getParentScreen() instanceof GridScreen)  // Double-check
                        {
                            JTable jtable = (JTable)this.getScreenField().getParentScreen().getScreenFieldView().getControl();
                            m_colorSelected = jtable.getSelectionBackground();
                        }
                        if (m_colorSelected == null)
                            m_colorSelected = control.getBackground();
                        if ((m_colorSelected == null) || (m_colorSelected.equals(Color.white)) || (m_colorSelected.equals(Color.black)))
                            m_colorSelected = Color.lightGray;
                    }
                    control.setBackground(m_colorSelected);
                    if (bGridControl)
                        if (this.getScreenField().getParentScreen() instanceof GridScreen)  // Double-check
                            if (bIsInput)
                                if (this.getScreenField().isEnabled())
                    {
                        JTable jtable = (JTable)this.getScreenField().getParentScreen().getScreenFieldView().getControl();
                        control.setBackground(jtable.getBackground());
                    }
                }
                if (screenInfo != null)
                    if (!screenInfo.isCustomTheme()) // If there is a custom theme, the theme will set the fonts
                        if (screenInfo.getFont() != null)
                            control.setFont(screenInfo.getFont());
            }
        }
    }
    /**
     * Get the background color for this control.
     * Note: I can't just always return the background, because edit boxes use the "control" background color.
     * @return The background color, or null if there is none.
     */
    public Color getControlBackgroundColor()
    {
        if (this.getScreenInfo() != null)
            return this.getScreenInfo().getBackgroundColor();
        else
            return null;
    }
    /**
     * The color of this background when selected.
     */
    private Color m_colorSelected = null;
    private Color m_colorNormal = null;
    /**
     * Synchronize the physical control with the ScreenField.
     */
    public void addPhysicalControl()
    {
        BasePanel parentScreen = this.getScreenField().getParentScreen();
        if (parentScreen != null)
        {
            Component control = (Component)this.getControl(DBConstants.CONTROL_TOP);
            Container panel = null;
            if (this.getScreenField() instanceof ToolScreen)
                panel = (Container)parentScreen.getScreenFieldView().getControl(DBConstants.CONTROL_TOP);
            if (panel == null) if (parentScreen.getScreenFieldView() != null)
                panel = (Container)parentScreen.getScreenFieldView().getControl(DBConstants.CONTROL_BOTTOM);
            if (panel == null) if (parentScreen.getScreenFieldView().getControl() == null)
            {   // Must have a parent control to create this control!
                if (parentScreen.getScreenFieldView() == null)
                    parentScreen.setScreenFieldView(parentScreen.setupScreenFieldView(true));
                parentScreen.getScreenFieldView().addPhysicalControl();
                panel = (Container)parentScreen.getScreenFieldView().getControl(DBConstants.CONTROL_BOTTOM);
            }
            if (panel != null) if (control != null)
            {
                if (parentScreen.getSField(parentScreen.getSFieldCount() - 1) != this.getScreenField())
                { // Add this in a different order (not to the end)
                    for (int i = 0; i < parentScreen.getSFieldCount(); i++)
                    {
                        if (parentScreen.getSField(i) == this.getScreenField())
                            panel.add(control, i);
                    }
                }
                else
                    panel.add(control);
                this.setControlAttributes(this.getControl(DBConstants.CONTROL_BOTTOM), true, false, false);   // Color, font, etc.
            }
        }
    }
    /**
     * Get the screen information.
     * @return The screen info.
     */
    public ScreenInfo getScreenInfo()
    {
        BasePanel parentScreen = this.getScreenField().getParentScreen();
        if (parentScreen != null)
            return ((VScreenField)parentScreen.getScreenFieldView()).getScreenInfo();
        else
            return null;
    }
    /**
     * Enable or disable this control.
     * @param bEnable If true, enable this field.
     */
    public void setEnabled(boolean bEnable)
    {
        if (this.getControl() != null)
            ScreenUtil.setEnabled(this.getControl(), bEnable);
    }
    /**
     * For the action listener (menu commands).
     * @param evt The action event.
     */
    public void actionPerformed(ActionEvent evt)
    {
        if (this.getScreenField().getConverter() != null)
        {
            this.getScreenField().controlToField();   // If linked to a field, Move the new value to the field
            if (m_componentEditor != null)
                this.stopCellEditing();    // Validate any cell being edited.
        }
        else
        {
        	this.validateCurrentFocus();	// Being careful... for example, keyboard accelerators do not call focusLost().
            String strCommand = evt.getActionCommand();
            BaseApplication application = (BaseApplication)this.getScreenField().getParentScreen().getTask().getApplication();
            if (application != null)
            {
                ResourceBundle resources = application.getResources(ResourceConstants.MENU_RESOURCE, true);
                if (resources instanceof Resources)  // Reverse lookup the command
                    strCommand = ((Resources)resources).getKey(strCommand);
            }

            if (strCommand != null)
            {
                int iIndex = strCommand.indexOf(' ');
                if (iIndex != -1)
                    strCommand = strCommand.substring(0, iIndex);
            }
            int bUseSameWindow = ScreenConstants.USE_SAME_WINDOW | DBConstants.PUSH_TO_BROSWER;
            if ((evt != null)
                && ((evt.getModifiers() & ActionEvent.SHIFT_MASK) != 0))
                    bUseSameWindow = ScreenConstants.USE_NEW_WINDOW | ScreenConstants.DONT_PUSH_TO_BROSWER;
            if (strCommand != null)
                this.getScreenField().handleCommand(strCommand, this.getScreenField(), bUseSameWindow);
            int iErrorCode = DBConstants.NORMAL_RETURN;
            if (this.getScreenField() != null)  // If the command was "close" the control will be gone.
                iErrorCode = this.getScreenField().controlToField();    // Code replaced with this
            if (iErrorCode != DBConstants.NORMAL_RETURN) if (this.getScreenField().getRootScreen() != null)
                this.getScreenField().getRootScreen().displayError(iErrorCode);
        }
    }
    /**
     *  This control gained focus (A Focus Listener).
     * @param evt The focus event.
     */
    public void focusGained(FocusEvent evt)
    {
        BaseField field = null;
        if (this.getScreenField().getConverter() != null) 
            field = (BaseField)this.getScreenField().getConverter().getField();   // This field changed
        Task task = this.getScreenField().getRootScreen().getTask();//getAppletScreen().getScreenFieldView().getControl();
        String strMessage = task.getStatusText(m_iLastErrorLevel + 1);    // Is there a higher level message being displayed?
        if ((strMessage == null) || (strMessage.equals(Constants.BLANK)))
        {
            if (m_iLastErrorLevel > DBConstants.INFORMATION)
                strMessage = task.getStatusText(m_iLastErrorLevel); // Get my waiting error message
            if (field != null)
                if ((strMessage == null) || (strMessage.equals(Constants.BLANK)))
                    strMessage = field.getFieldTip();
            if (strMessage != null)
                if (!m_bDisableMessages)
                    task.setStatusText(strMessage, m_iLastErrorLevel);      // Display either the last message or the field tip
        }

        if (field != null)
            this.getScreenField().getRootScreen().getAppletScreen().undoTargetGained(this.getScreenField());
    }
    /**
     * This control lost focus (A Focus Listener).
     * @param evt The focus event.
     */
    public void focusLost(FocusEvent evt)
    {
        int iErrorCode = this.getScreenField().controlToField();    // Code replaced with this
        JTable jTable = null;
        if (this.getScreenField() != null)
            if (this.getScreenField().getParentScreen() != null)
                if (this.getScreenField().getParentScreen().getScreenFieldView() != null)
                    if (this.getScreenField().getParentScreen().getScreenFieldView().getControl() instanceof JTable)
                        jTable = (JTable)this.getScreenField().getParentScreen().getScreenFieldView().getControl();
        if (listenerList != null)
            if (jTable != null)
                if (evt.getOppositeComponent() != jTable)
                    this.stopCellEditing();     // Validate data in this field if you are clicking away from this table
        Task task = null;
        if (this.getScreenField() != null)
            if (this.getScreenField().getRootScreen() != null)
                task = this.getScreenField().getRootScreen().getTask();
        if (task != null)
        {
            if (iErrorCode != DBConstants.NORMAL_RETURN)
            {
                this.getScreenField().fieldToControl();   // Redisplay this field value.
                task.setStatusText(task.getLastError(iErrorCode), DBConstants.WARNING);   // Make sure this is displayed next time.
                m_iLastErrorLevel = DBConstants.WARNING;
                this.requestFocus();    // Re-enter this field
            }
            else
            {
                if (m_iLastErrorLevel > DBConstants.INFORMATION)
                {   // Clean up this error flag when I exit.
                    task.setStatusText(task.getLastError(iErrorCode), DBConstants.INFORMATION);   // Make sure this is displayed next time.
                    m_iLastErrorLevel = DBConstants.INFORMATION;
                }
            }
        }
    }
    int m_iLastErrorLevel = DBConstants.INFORMATION;
    /**
     * For rendering this component in a grid (TableCellRenderer method).
     * @param table The table.
     * @param value The cell's value.
     * @param isSelected Is the cell selected.
     * @param hasFocus Does the cell have focus?
     * @param row The cell row.
     * @param column The cell column.
     */
    public Component getTableCellRendererComponent(JTable table,
                                                         Object value,
                                                         boolean isSelected,
                                                         boolean hasFocus,
                                                         int row,
                                                         int column)
    {
        JComponent control = this.getRendererControl();
        this.setComponentState(control, value);   // Set this component
        this.setControlAttributes(control, false, isSelected, true);
        if (control.getBorder() == m_focusBorder)
            control.setBorder(null);
        if (isSelected) if (hasFocus)
        {
            if (control.getBorder() == null)
                control.setBorder(m_focusBorder);
            if (!table.isCellEditable(row, column))
                isSelected = false;
            this.setControlAttributes(control, true, isSelected, true);     // This is the user's cue that this is the focused component (no cursor yet)
        }
        return control;
    }
    /**
     * Returns the value contained in the editor.
     * <p>Note: This is the value passed to Table.setValue() which is expecting a String unless
     * a string is not available, then a data object is okay.
     * @return The component's value.
     */
    public Object getCellEditorValue()
    {
        return this.getComponentState(this.getEditorControl());   // Get the value of this component
    }
    /**
     * Ask the editor if it can start editing using anEvent. anEvent is in the invoking component coordinate system. The editor
     * can not assume the Component returned by getCellEditorComponent() is installed. This method is intended for the use of
     * client to avoid the cost of setting up and installing the editor component if editing is not possible. If editing can be started
     * this method returns true. 
     * @param anEvent - the event the editor should use to consider whether to begin editing or not. 
     * @return true if editing can be started. 
     */
    public boolean isCellEditable(EventObject anEvent)
    {
        return this.getScreenField().isEnabled();  // If enabled, allow editing
    }
    /**
     * Tell the editor to start editing using anEvent. It is up to the editor if it want to start editing in different states depending on
     * the exact type of anEvent. For example, with a text field editor, if the event is a mouse event the editor might start editing
     * with the cursor at the clicked point. If the event is a keyboard event, it might want replace the value of the text field with
     * that first key, etc. anEvent is in the invoking component's coordinate system. A null value is a valid parameter for
     * anEvent, and it is up to the editor to determine what is the default starting state. For example, a text field editor might
     * want to select all the text and start editing if anEvent is null. The editor can assume the Component returned by
     * getCellEditorComponent() is properly installed in the clients Component hierarchy before this method is called. 
     *
     * The return value of shouldSelectCell() is a boolean indicating whether the editing cell should be selected or not. Typically,
     * the return value is true, because is most cases the editing cell should be selected. However, it is useful to return false to
     * keep the selection from changing for some types of edits. eg. A table that contains a column of check boxes, the user
     * might want to be able to change those checkboxes without altering the selection. (See Netscape Communicator for just
     * such an example) Of course, it is up to the client of the editor to use the return value, but it doesn't need to if it doesn't
     * want to. 
     *
     * @param anEvent the event the editor should use to start editing. 
     * @return  true if the editor would like the editing cell to be selected 
     */
    public boolean shouldSelectCell(EventObject anEvent)
  {
        if (anEvent != null) if (anEvent instanceof MouseEvent)
            if (this.getScreenField() instanceof SEditText)
        { // If text field, set selection to current point
        }
        boolean bSelectCell = this.getScreenField().isEnabled();
        if (bSelectCell)
            bSelectCell = this.getScreenField().isFocusTarget();
        return bSelectCell;   // If enabled, allow editing
    }
    /**
     * Sets an initial value for the editor. This will cause the editor to stopEditing and lose any partially edited value if the editor
     * is editing when this method is called. 
     *
     * Returns the component that should be added to the client's Component hierarchy. Once installed in the client's hierarchy
     * this component will then be able to draw and receive user input. 
     *
     * Parameters: 
     *    table - the JTable that is asking the editor to edit This parameter can be null. 
     *    value - the value of the cell to be edited. It is up to the specific editor to interpret and draw the value. eg. if value is
     *    the String "true", it could be rendered as a string or it could be rendered as a check box that is checked. null is a
     *    valid value. 
     *    isSelected - true if the cell is to be renderer with selection highlighting 
     *    column - the column identifier of the cell being edited 
     *    row - the row index of the cell being edited 
     * Returns: 
     *     the component for editing
     * NOTE: Possible concurrency problem... May want to double-check to be sure the correct record is current.
     * @param table The table.
     * @param value The cell's value.
     * @param isSelected Is the cell selected.
     * @param hasFocus Does the cell have focus?
     * @param row The cell row.
     * @param column The cell column.
     */
    public Component getTableCellEditorComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       int row,
                                                       int column)
    {
        if (listenerList == null)
            listenerList = new EventListenerList();

        JComponent control = this.getEditorControl();
        this.setComponentState(control, value);   // Set this component

        isSelected = true;
        if (!table.isCellEditable(row, column))
            isSelected = false;
        this.setControlAttributes(control, isSelected, isSelected, true);
        if (control.getBorder() == null)
            control.setBorder(m_focusBorder);
        if (table.getSelectedRow() != row)
            table.setRowSelectionInterval(row, row);
        return control;
    }
    /**
     * Get the editor control.
     * This logic is kind of complicated.
     * If the renderer control has not been set up yet, set it up.
     * Then, set the editor control to the current control and disconnect it.
     * NOTE: The control is disconnected from the field to keep record move operations from doing a fieldToControl().
     * @return The editor control.
     */
    public JComponent getEditorControl()
    {
        if (m_componentRender == null)
        {
            m_componentRender = (JComponent)this.setupControl(false); // Create a non-editable copy for a renderer
            ((JComponent)m_componentRender).setOpaque(true);    // Grid components can't be transparent
            m_componentEditor = (JComponent)this.getControl();
            this.setControl(null);  // Disconnect the control from the field
        }
        return m_componentEditor;
    }
    /**
     * Get the renderer control.
     * Note: To save horsepower, if an editor has not been used yet, keep control connected and use it!
     * @return The renderer control.
     */
    public JComponent getRendererControl()
    {
        JComponent componentRender = (JComponent)this.getControl();
        if (m_componentRender != null)
            componentRender = m_componentRender;
        return componentRender;
    }
    /**
     * Can I stop cell editing?
     * @return True always as I can always stop cell editing.
     */
    public boolean stopCellEditing()
    {
        fireEditingStopped();
        return true;
    }
    /**
     * Cancel the cell editing.
     */
    public void cancelCellEditing()
    {
        fireEditingCanceled();
    }
    /**
     * Add this cell editor listener.
     * @param l The listener to add to the list.
     */
    public void addCellEditorListener(CellEditorListener l)
    {
        listenerList.add(CellEditorListener.class, l);
    }
    /**
     * Remove this cell editor listener.
     * @param l The listener to Remove from the list.
     */
    public void removeCellEditorListener(CellEditorListener l)
    {
        listenerList.remove(CellEditorListener.class, l);
    }
    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireEditingStopped()
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==CellEditorListener.class) {
            // Lazily create the event:
            if (changeEvent == null)
                changeEvent = new ChangeEvent(this);
            ((CellEditorListener)listeners[i+1]).editingStopped(changeEvent);
            }        
        }
    }
    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireEditingCanceled()
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==CellEditorListener.class) {
            // Lazily create the event:
            if (changeEvent == null)
                changeEvent = new ChangeEvent(this);
            ((CellEditorListener)listeners[i+1]).editingCanceled(changeEvent);
            }        
        }
    }
    /**
     *  The user pressed a key (A Key Listener).
     * @param evt The key event.
     */
    public void keyPressed(KeyEvent evt)
    {
        int key = evt.getKeyCode();
        if ((key == KeyEvent.VK_ENTER) && (!evt.isShiftDown()) && (!evt.isAltDown()) && (!evt.isControlDown()))
        {   // Enter with no modifiers = tab to next
            if (this.getControl() != null)
                if (((JComponent)this.getControl()).getRootPane() != null)
                    if (((JComponent)this.getControl()).getRootPane().getDefaultButton() != null)
            {   // Special case - Press return = do default command
                JButton button = ((JComponent)this.getControl()).getRootPane().getDefaultButton();
                button.doClick();
                return;
            }
            this.getScreenField().selectField(this.getScreenField(), DBConstants.SELECT_NEXT_FIELD);
            evt.consume();  // Consume this key
        }
    }
    /**
     *  The user released a key (A Key Listener).
     * @param evt The key event.
     */
    public void keyReleased(KeyEvent evt)
    {
    }
    /**
     *  The user typed a key (A Key Listener).
     * @param evt The key event.
     */
    public void keyTyped(KeyEvent evt)
    {
    }
    /**
     *  Item changed (Needed to implements ItemListener).
     * @param evt The item event.
     */
    public void itemStateChanged(ItemEvent evt)
    {
        if (evt.getStateChange() == ListDataEvent.INTERVAL_REMOVED)
            return; // Ignore this
        int iErrorCode = this.getScreenField().controlToField();    // Code replaced with this
        if (iErrorCode != DBConstants.NORMAL_RETURN) if (this.getScreenField().getRootScreen() != null)
            this.getScreenField().getRootScreen().displayError(iErrorCode);
    }
    /**
     * User pressed down on the mouse button (MouseListener method).
     * @param evt The mouse event.
     */
    public void mousePressed(MouseEvent evt)
    {
        // Override to handle this
    }
    /**
     * User released the mouse button (MouseListener method).
     * @param evt The mouse event.
     */
    public void mouseReleased(MouseEvent e)
    {
        long lMouseLastClick = m_lMouseLastClick;
        m_lMouseLastClick = System.currentTimeMillis();
        if ((m_lMouseLastClick - lMouseLastClick) < kiDoubleClickTimems)
            this.onDoubleClick(e);
        else
            this.onMouseClicked(e);
    }
    /**
     * User clicked the mouse button.
     * @param evt The mouse event.
     */
    public void onMouseClicked(MouseEvent evt)
    {
    }
    /**
     * User pressed down on the mouse button.
     * @param evt The mouse event.
     */
    public void onDoubleClick(MouseEvent evt)
    {
        // Override to handle this
    }
    /**
     * Invoked when the mouse has been clicked on a component (MouseListener method).
     * @param evt The mouse event.
     */
    public void mouseClicked(MouseEvent evt)
    {
    }
    /**
     * Invoked when the mouse enters a component (MouseListener method).
     * @param evt The mouse event.
     */
    public void mouseEntered(MouseEvent evt)
    {
    }
    /**
     * Invoked when the mouse exits a component (MouseListener method).
     * @param evt The mouse event.
     */
    public void mouseExited(MouseEvent evt)
    {
    }
    /**
     * If someone requests a different focus here, wait for the end!
     */
    private static ScreenField gsFieldFocus = null;
    /**
     * If someone requests a different focus here, wait for the end!
     */
    private static boolean m_bDisableMessages = false;
    /**
     * Give this control the input focus.
     * @return true If focus is gained.
     */
    public boolean requestFocus()
    {
        if (!this.getScreenField().isEnabled())
            return false;
        if (this.getControl() != null)
        {
            if (this.getScreenField().getParentScreen().getScreenFieldView() instanceof VGridScreen)
            {
                return ((VGridScreen)((GridScreen)this.getScreenField().getParentScreen()).getScreenFieldView()).requestFocus(this.getScreenField()); // Special tab listener for grid screens
            }
            else
            {
            // START SPECIAL CODE: If someone requests a different focus while I lose focus,
            // wait for the next focus, then set the focus back to the target.
                if (gsFieldFocus == null)
                {
                    gsFieldFocus = this.getScreenField();
                    if (this.getControl().hasFocus())
                        this.getScreenField().controlToField();   // Special case - I already have focus, so validate field manually.
                    else
                        this.getControl().requestFocus(); // Not part of SPECIAL CODE
                    if (gsFieldFocus != this.getScreenField())
                    {   // Focus changed during lose focus
                        ScreenField sField = gsFieldFocus;
                        gsFieldFocus = null;
                        sField.getScreenFieldView().requestFocus();   // re-request the focus
                    }
                    gsFieldFocus = null;
                    m_bDisableMessages = false;
                }
                else
                {
                    m_bDisableMessages = true;
                    gsFieldFocus = this.getScreenField();           // In the middle of setting another focus...upon return ,set focus to this
                }
            // END SPECIAL CODE
            }
        }
        return true;    // Click handled
    }
    /**
     * Request focus?
     * @param bIsFocusTarget Is this the focus target.
     */
    public void setRequestFocusEnabled(boolean bIsFocusTarget)
    {
        if (this.getControl() != null)
            ((JComponent)this.getControl()).setRequestFocusEnabled(bIsFocusTarget);
    }
    /**
     * This is a utility method to show an HTML page.
     * @param strURL The URL of the document.
     */
    public boolean showDocument(String strURL, int iOptions)
    {
        return this.getScreenField().getParentScreen().getAppletScreen().getScreenFieldView().showDocument(strURL, iOptions);
    }
    /**
     * Load this image.
     * @param filename The image file name.
     * @param description The image description.
     * @return The image icon.
     */
    public ImageIcon loadImageIcon(String filename, String description)
    {
        return ((VAppletScreen)this.getScreenField().getParentScreen().getAppletScreen().getScreenFieldView()).loadImageIcon(filename, description);
    }
    /**
     * Validate the currently focused component (This is a 1.4 HACK).
     * @return A validation error code.
     */
    public int validateCurrentFocus()
    {
        int iErrorCode = DBConstants.NORMAL_RETURN;
        Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (component != null)
        {
            ScreenField sField = VScreenField.getComponentModel(component);
            if (sField != null)
            {
            	if (!(sField instanceof SButtonBox))	// Validating a button box would simulate pressing the button (don't)
            		iErrorCode = sField.controlToField();    // Validate the current field.
                if (sField.getConverter() != null)
                    if (sField.getScreenFieldView().getControl() == null)
                        if (sField.getParentScreen() instanceof BaseGridScreen)
                            if (sField.getScreenFieldView() instanceof VScreenField)    // Always
                {
                    ((VScreenField)sField.getScreenFieldView()).stopCellEditing();
                    ((VBaseGridScreen)sField.getParentScreen().getScreenFieldView()).finalizeThisScreen();
                }
            }
        }
        return iErrorCode;
    }
    /**
     * Get the screen field model for this component.
     * @param aComponent The component to get the model for.
     * @return The screenfield for this component (or null if it doesn't exist).
     */
    public static ScreenField getComponentModel(Component aComponent)
    {
        FocusListener[] listeners = aComponent.getFocusListeners();
        if (listeners != null)
        {
            for (int i = 0; i < listeners.length; i++)
            {
                FocusListener listener = listeners[i];
                if (listener instanceof VScreenField)
                    return (ScreenField)((VScreenField)listener).getScreenField();
            }
        }
        return null;    // Not a swing view
    }
    /**
     * Is this recordowner a batch process, or an interactive screen?
     * @return True if this is a batch process.
     */
    public boolean isBatch()
    {
        return false;   // No, swing components are interactive.
    }
    /**
     * Get the name of the stylesheet.
     * If no path, assumes docs/styles/.
     * If no extension, assumes .xsl.
     * @return The name of the stylesheet.
     */
    public String getStylesheetPath()
    {
    	return null;	// Not used in swing
    }
}
