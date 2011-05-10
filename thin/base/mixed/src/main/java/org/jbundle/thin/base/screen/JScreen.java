package org.jbundle.thin.base.screen;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;

import org.jbundle.model.db.FieldComponent;
import org.jbundle.model.db.ScreenComponent;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.FieldInfo;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.FieldTable;
import org.jbundle.thin.base.screen.action.ActionManager;


/**
 * A Basic data entry screen.
 * This screen is made of a panel with a GridBagLayout. Labels in the first column, aligned right.
 * Data fields in the second column aligned left.
 */
public class JScreen extends JBaseScreen
    implements FocusListener
{
	private static final long serialVersionUID = 1L;

	/**
     * Next component to focus on.
     */
    protected Component m_componentNextFocus = null;
    /**
     * The grid bag layout.
     */
    protected LayoutManager m_layout = null;
    /**
     * The grid bag constraints (so I don't have to keep creating new objects).
     */
    protected GridBagConstraints m_gbconstraints = null;
    /**
     * The code to create a control.
     */
    public static final int CONTROL = 1;
    /**
     * The code to create a label.
     */
    public static final int LABEL = 0;
    /**
     * A lined border (shared object).
     */
    public static Border m_borderLine = LineBorder.createGrayLineBorder();

    /**
     *  JScreen Class Constructor.
     */
    public JScreen()
    {
        super();
    }
    /**
     *  JScreen Class Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param record and the record or GridTableModel as the parent.
     */
    public JScreen(Object parent, Object record)
    {
        this();
        this.init(parent, record);
    }
    /**
     * Initialize this class.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param record and the record or GridTableModel as the parent.
     */
    public void init(Object parent, Object record)
    {
        super.init(parent, record);

        this.addSubPanels(this);

        this.fieldsToControls();
    }
    /**
     * Free the resources held by this object.
     * Besides freeing all the sub-screens, this method disconnects all of my
     * fields from their controls.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Get the maximum size of this screen.
     * This special logic keeps the screen from centering the components. It sets the maximum size to the
     * preferred (calculated) size.
     * @return the maximum size.
     */
    public Dimension getMaximumSize()
    {
        return this.getPreferredSize();
    }
    /**
     * Add any screen sub-panel(s) now.
     * You might want to override this to create an alternate parent screen.
     * @param parent The parent to add the new screen to.
     * @return true if success
     */
    public boolean addSubPanels(Container parent)
    {
        if (parent == null)
            parent = this;

        LayoutManager layout = this.getScreenLayout(parent);
        parent.setLayout(layout);

        if (this.getFieldList() != null)
        {
// Column 0: (Labels)
            this.addScreenLabels(parent);
// Column 1 and 2: (Edit boxes)
            this.addScreenControls(parent);
        }
        return true;
    }
    /**
     * Get the GridBagConstraints.
     * @return The gridbag constraints object.
     */
    public final LayoutManager getScreenLayout()
    {
        return this.getScreenLayout(null);
    }
    public LayoutManager getScreenLayout(Container parent)
    {
        if (m_layout == null)
            m_layout = new GridBagLayout();
        return m_layout;
    }
    /**
     * Get the GridBagConstraints.
     * @return The gridbag constraints object.
     */
    public GridBagConstraints getGBConstraints()
    {
        if (m_gbconstraints == null)
        {
            m_gbconstraints = new GridBagConstraints();
            m_gbconstraints.insets = new Insets(2, 2, 2, 2);
            m_gbconstraints.ipadx = 2;
            m_gbconstraints.ipady = 2;
        }
        return m_gbconstraints;
    }
    /**
     * Set the constraints for this component.
     * If you aren't using gridbag, override this.
     * @param component The component to set the constraints for.
     */
    public void setComponentConstraints(JComponent component)
    {
        GridBagConstraints c = this.getGBConstraints();
        GridBagLayout gridbag = (GridBagLayout)this.getScreenLayout();
        gridbag.setConstraints(component, c);
    }
    /**
     * Add the description labels to the first column of the grid.
     * @param parent The container to add the control(s) to.
     * @param gridbag The screen layout.
     * @param c The constraint to use.
     */
    public void addScreenLabels(Container parent)
    {
        GridBagConstraints c = this.getGBConstraints();
        c.weightx = 0.0;                        // Minimum width to hold labels
        c.anchor = GridBagConstraints.NORTHEAST;    // Labels right justified
        c.gridx = 0;                            // Column 0
        c.gridy = GridBagConstraints.RELATIVE;  // Bump Row each time
        for (int iIndex = 0; ; iIndex++)
        {
            Converter converter = this.getFieldForScreen(iIndex);
            if (converter == SKIP_THIS_FIELD)
                continue;
            if (converter == null)
                break;
            this.addScreenLabel(parent, converter);
        }
    }
    /**
     * Add this label to the first column of the grid.
     * @param parent The container to add the control(s) to.
     * @param fieldInfo The field to add a label to.
     * @param gridbag The screen layout.
     * @param c The constraint to use.
     */
    public JComponent addScreenLabel(Container parent, Converter fieldInfo)
    {
        JComponent label = new JLabel(fieldInfo.getFieldDesc());
        this.setComponentConstraints(label);
        parent.add(label);
        fieldInfo.addComponent(label);
        return label;
    }
    /**
     * Add the screen controls to the second column of the grid.
     * @param parent The container to add the control(s) to.
     * @param gridbag The screen layout.
     * @param c The constraint to use.
     */
    public void addScreenControls(Container parent)
    {
        GridBagConstraints c = this.getGBConstraints();
        c.weightx = 1.0;                        // Grow edit and scroll pane but not label
        c.anchor = GridBagConstraints.NORTHWEST;    // Edit boxes left justified
        c.gridx = 1;                            // Column 1
        c.gridy = GridBagConstraints.RELATIVE;  // Bump Row each time
        c.gridwidth = 3; // end column
        for (int iIndex = 0; ; iIndex++)
        {
            Converter converter = this.getFieldForScreen(iIndex);
            if (converter == SKIP_THIS_FIELD)
                continue;
            if (converter == null)
                break;
            this.addScreenControl(parent, converter);
        }
    }
    /**
     * Add the screen controls to the second column of the grid.
     * @param parent The container to add the control(s) to.
     * @param fieldInfo the field to add a control for.
     * @param gridbag The screen layout.
     * @param c The constraint to use.
     */
    public JComponent addScreenControl(Container parent, Converter fieldInfo)
    {
        JComponent component = this.createScreenComponent(fieldInfo);
        if (component instanceof ScreenComponent)
            if (((ScreenComponent)component).getConverter() != null)
                fieldInfo = (Converter)((ScreenComponent)component).getConverter();
        this.addScreenComponent(parent, fieldInfo, component);
        return component;
    }
    /**
     * Add the screen controls to the second column of the grid.
     * Create a default component for this fieldInfo.
     * @param fieldInfo the field to create a control for.
     * @return The component.
     */
    public JComponent createScreenComponent(Converter fieldInfo)
    {
        int iRows = 1;
        int iColumns = fieldInfo.getMaxLength();
        if (iColumns > 40)
        {
            iRows = 3;
            iColumns = 30;
        }
        String strDefault = fieldInfo.toString();
        if (strDefault == null)
            strDefault = Constants.BLANK;

        JComponent component = null;
        if (iRows <= 1)
        {
            component = new JTextField(strDefault, iColumns);
            if (fieldInfo instanceof FieldInfo)
                if (Number.class.isAssignableFrom(((FieldInfo)fieldInfo).getDataClass()))
                    ((JTextField)component).setHorizontalAlignment(JTextField.RIGHT);
        }
        else
        {
            component = new JTextArea(strDefault, iRows, iColumns);
            component.setBorder(m_borderLine);
        }
        return component;
    }
    /**
     * Add the screen controls to the second column of the grid.
     * @param parent The container to add the control(s) to.
     * @param fieldInfo the field to add a control for.
     * @param component The component to add.
     * @param gridbag The screen layout.
     * @param c The constraint to use.
     */
    public void addScreenComponent(Container parent, Converter fieldInfo, JComponent component)
    {
        String strName = fieldInfo.getFieldName();  // Almost always
        if (strName != null)
            component.setName(strName);
        component.addFocusListener(this);
        // todo(don) Note: I never remove this focus listener.
        this.setComponentConstraints(component);
        parent.add(component);
        fieldInfo.addComponent(component);
    }
    /**
     * Add a submit and reset buttons to the bottom of the grid.
     * @param gridbag The screen layout.
     * @param c The constraint to use.
     */
    public void addScreenButtons(Container parent)
    {
        GridBagLayout gridbag = (GridBagLayout)this.getScreenLayout();
        GridBagConstraints c = this.getGBConstraints();
        c.gridheight = GridBagConstraints.REMAINDER; // end row
        c.anchor = GridBagConstraints.NORTH;
        c.gridwidth = 1; // end column
        ImageIcon icon = BaseApplet.getSharedInstance().loadImageIcon(Constants.SUBMIT);
        JButton button = new JButton(Constants.SUBMIT, icon);
        button.setOpaque(false);
        button.setName(Constants.SUBMIT);
        gridbag.setConstraints(button, c);
        parent.add(button);
        button.addActionListener(this);
        c.gridx = 3;                            // Column 1
        c.gridheight = GridBagConstraints.REMAINDER; // end row
        c.anchor = GridBagConstraints.NORTH;
        icon = BaseApplet.getSharedInstance().loadImageIcon(Constants.RESET);
        button = new JButton(Constants.RESET, icon);
        button.setOpaque(false);
        button.setName(Constants.RESET);
        gridbag.setConstraints(button, c);
        parent.add(button);
        c.gridheight = 1; //Set back
        button.addActionListener(this);
    }
    /**
     * Move the screen controls to the data record(s).
     * This is usually not necessary.
     */
    public void controlsToFields()
    {
        for (int iFileSeq = 0; ; iFileSeq++)
        {
            FieldList fieldList = this.getFieldList(iFileSeq);
            if (fieldList == null)
                break;
            for (int iFieldSeq = 0; ; iFieldSeq++)
            {
                Converter converter = fieldList.getField(iFieldSeq);
                if (converter == null)
                    break;
                FieldInfo field = converter.getField();
                for (int iSFieldSeq = 0; ; iSFieldSeq++)
                {
                    Object component = field.getComponent(iSFieldSeq);
                    if (component == null)
                        break;
                    if (component instanceof FieldComponent)
                        field.setData(((FieldComponent)component).getControlValue());
                    else if (component instanceof JTextComponent)
                        field.setString(((JTextComponent)component).getText());
                }
            }
        }
    }
    /**
     * Move the data record(s) to the screen controls.
     * This is usually not necessary, used only when a screen is first displayed.
     */
    public void fieldsToControls()
    {
        for (int iFileSeq = 0; ; iFileSeq++)
        {
            FieldList fieldList = this.getFieldList(iFileSeq);
            if (fieldList == null)
                break;
            for (int iFieldSeq = 0; ; iFieldSeq++)
            {
                Converter converter = fieldList.getField(iFieldSeq);
                if (converter == null)
                    break;
                FieldInfo field = converter.getField();
                field.displayField();   // This will move the data to all the screen components
            }
        }
    }
    /**
     * Clear all the fields to their default values.
     */
    public void resetFields()
    {
        for (int iFileSeq = 0; ; iFileSeq++)
        {
            FieldList fieldList = this.getFieldList(iFileSeq);
            if (fieldList == null)
                break;
            for (int iFieldSeq = 0; ; iFieldSeq++)
            {
                Converter converter = fieldList.getField(iFieldSeq);
                if (converter == null)
                    break;
                FieldInfo field = converter.getField();
                field.initField(Constants.DISPLAY);   // Init the field and display
            }
        }
    }
    /**
     * Required as part of the FocusListener interface.
     * @param e The focus event.
     */
    public void focusGained(FocusEvent e)
    {
        if (m_componentNextFocus != null)
        {
            m_componentNextFocus.requestFocus();
            return;
        }
        Component component = (Component)e.getSource();
        String string = component.getName();
        if (this.getFieldList() != null)
        {
            FieldInfo field = this.getFieldList().getField(string);        // Get the fieldInfo for this component
            if (field != null)
                this.getBaseApplet().setStatusText(field.getFieldTip(), Constants.INFORMATION);
        }
        String strLastError = BaseApplet.getSharedInstance().getLastError(0);
        if ((strLastError != null) && (strLastError.length() > 0))
            this.getBaseApplet().setStatusText(strLastError, Constants.WARNING);
    }
    /**
     * When a control loses focus, move the field to the data area.
     * @param e The focus event.
     */
    public void focusLost(FocusEvent e)
    {
        m_componentNextFocus = null;
        Component component = (Component)e.getSource();
        String string = component.getName();
        FieldInfo field = null;
        if (this.getFieldList() != null)
            field = this.getFieldList().getField(string);        // Get the fieldInfo for this component
        if (field != null)
        {
            int iErrorCode = Constants.NORMAL_RETURN;
            if (component instanceof FieldComponent)
                iErrorCode = field.setData(((FieldComponent)component).getControlValue(), Constants.DISPLAY, Constants.SCREEN_MOVE);
            else if (component instanceof JTextComponent)
                iErrorCode = field.setString(((JTextComponent)component).getText(), Constants.DISPLAY, Constants.SCREEN_MOVE);
            if (iErrorCode != Constants.NORMAL_RETURN)
            {
                field.displayField();               // Redisplay the old field value
                m_componentNextFocus = component;   // Next focus to this component
            }
        }
    }
    /**
     * Focus to the first field.
     */
    public void resetFocus()
    {
        for (int i = 0; ; i++)
        {
            Converter converter = this.getFieldForScreen(i);
            if (converter == SKIP_THIS_FIELD)
                continue;
            if (converter == null)
                break;
            if (converter instanceof FieldInfo)
                if (((FieldInfo)converter).getComponent(CONTROL) != null)
                {
                    ((Component)((FieldInfo)converter).getComponent(CONTROL)).requestFocus();
                    return;
                }
        }
    }
    /**
     * Process this action.
     * @param strAction The action to process.
     * By default, this method handles RESET, SUBMIT, and DELETE.
     */
    public boolean doAction(String strAction, int iOptions)
    {
        FieldList fieldList = this.getFieldList();
        FieldTable fieldTable = null;
        if (fieldList != null)
            fieldTable = fieldList.getTable();
        boolean bFlag = false;
        try   {
            if (Constants.RESET.equalsIgnoreCase(strAction))
            {
                if (fieldTable != null)
                {
                    fieldTable.addNew();
                }
                this.resetFocus();
                bFlag = true;
            }
            bFlag = super.doAction(strAction, iOptions);
            if (bFlag == false)
                if (Constants.GRID.equalsIgnoreCase(strAction))
            {
                try   {
                    JBaseScreen screen = this.createGridScreen(this.getFieldList());
                    if (screen != null)
                    {
                        Container parent = null;
                        this.getFieldList().setOwner(screen);    // The record belongs to the new screen
                        if (screen.getParentObject() instanceof Container)
                            parent = (Container)screen.getParentObject();
                        this.getBaseApplet().changeSubScreen(parent, screen, null);
                        bFlag = true;
                    }
                } catch (Exception ex)  {
                    ex.printStackTrace();
                }
            }
            if (Constants.SUBMIT.equalsIgnoreCase(strAction))
            {
                if (fieldTable != null)
                {
                    if (fieldList.isModified())
                    {
                        if ((fieldList.getEditMode() == Constants.EDIT_IN_PROGRESS) ||
                            (fieldList.getEditMode() == Constants.EDIT_CURRENT))
                        {
                            fieldTable.set(fieldList);
                        }
                        else
                        {
                            fieldTable.add(fieldList);
                        }
                    }
                    fieldTable.addNew();
                    this.resetFocus();
                    bFlag = true;
                }
            }
            else if (Constants.DELETE.equalsIgnoreCase(strAction))
            {
                if (fieldTable != null)
                {
                    if ((fieldList.getEditMode() == Constants.EDIT_IN_PROGRESS) ||
                        (fieldList.getEditMode() == Constants.EDIT_CURRENT))
                    {
                        fieldTable.remove();
                    }
                    fieldTable.addNew();
                    this.resetFocus();
                    bFlag = true;
                }
            }
            else if (Constants.RESET.equalsIgnoreCase(strAction))
            {
                bFlag = true;   // Handled by this screen
            }
        } catch (Exception ex)  {
            this.getBaseApplet().setStatusText(ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            bFlag = true;   // Handled (with an error).
        }
        return bFlag;
    }
    /**
     * Read from this record's default key.
     * In order to get this to work, you need to add some code like this:
     * <pre>
     *  boolean bFirstChange = false;
     *  if (this.getFieldList() != null)
     *      if (this.getFieldList().getEditMode() == Constants.EDIT_ADD)
     *          if (this.getFieldList().isModified() == false)
     *              bFirstChange = true;
     *  super.focusLost(e);
     *  if (bFirstChange)
     *  {
     *      m_componentNextFocus = null;
     *      Component component = (Component)e.getSource();
     *      String string = component.getName();
     *      FieldInfo field = m_vFieldList.getFieldInfo(string);        // Get the fieldInfo for this component
     *      if (field != null)
     *          if ("TestCode".equals(string))
     *          {
     *              this.getFieldList().setKeyName("TestCode");
     *              this.readKeyed(field);
     *          }
     *  }
     * </pre>
     * @return boolean success if successful (if not, does an addnew())
     */
    public boolean readKeyed(FieldInfo field)
    {
        boolean bSuccess = false;
        String strValue = field.getString();
        FieldList fieldList = this.getFieldList();
        FieldTable fieldTable = null;
        if (fieldList != null)
            fieldTable = fieldList.getTable();
        if (strValue != null)
            if (strValue.length() > 0)
                if (fieldTable != null)
        {
            try   {
                if (!(bSuccess = fieldTable.seek(null)))
                {
                    fieldTable.addNew();
                    field.setString(strValue);
                }
            } catch (Exception ex)  {
                this.getBaseApplet().setStatusText(ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        }
        return bSuccess;
    }
    /**
     * Add the scrollbars?
     * For maint screens, default to true.
     */
    public boolean isAddScrollbars()
    {
        return true;
    }
    /**
     * Add the toolbars?
     * @return The newly created toolbar or null.
     */
    public JComponent createToolbar()
    {
        return new JScreenToolbar(this, null);
    }
    /**
     * Add the menubars?
     * @return The newly created menubar or null.
     */
    public JMenuBar createMenubar()
    {
        JMenuBar menuBar = ActionManager.getActionManager().setupStandardMenu(this);
        
        return menuBar;
    }
    /**
     * Get the command string that can be used to create this screen.
     * @return The screen command (defaults to ?applet=&screen=xxx.xxx.xxxx).
     */
    public String getScreenCommand()
    {
        String strCommand = super.getScreenCommand();
        FieldList record = this.getFieldList();
        if (record != null)
            if ((record.getEditMode() == Constants.EDIT_IN_PROGRESS)
                || (record.getEditMode() == Constants.EDIT_CURRENT))
                    strCommand += "&objectID=" + record.getCounterField().toString();
        return strCommand;
    }
    /**
     * Create a grid screen for this form.
     * @param record the (optional) record for this screen.
     * @return The new grid screen.
     */
    public JBaseScreen createGridScreen(FieldList record)
    {
        return null;
    }
}
