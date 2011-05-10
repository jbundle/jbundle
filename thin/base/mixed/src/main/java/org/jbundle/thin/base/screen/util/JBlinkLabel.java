/**
 * JBlinkLabel.java
 *
 * Created on June 17, 2000, 6:47 AM
 */
 
package org.jbundle.thin.base.screen.util;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jbundle.model.db.FieldComponent;
import org.jbundle.thin.base.db.Converter;


/** 
 * JBlinkLabel Alternates images every 1/2 second.
 * This control is typically used to indicate that something is being processed.
 * This control cycles through the icons depending on the bitmap retrieved from the Model.
 * Each bit set, corresponds to an Icon in the table. If bit 0 is set, the 0 icon is
 * displayed on every other click.
 * (ie., 0011001 will cycle - img0, img3, img0, img4, img0, etc)
 * (ie., 0011100 will cycle - img2, img3, img4, img2, etc)
 * WARNING WARNING WARNING - Do not use this control in a JTable unless your TableModel's
 * data is cached, because each time the image changes, the JTable's cell is re-retrieved.
 * NOTE: You may want to override a few methods described in AbstractCellRenderer, to minimize
 * the validate storm.
 * @author  Administrator
 * @version 1.0.0
 */
public class JBlinkLabel extends JLabel
    implements TableCellRenderer, ActionListener, FieldComponent
{
	private static final long serialVersionUID = 1L;

	/**
     * Maximum icons.
     */
    private static final int MAX_ICONS = 16;
    /**
     * A cached copy of the table model (for JTable implementations).
     */
    protected JTable m_table = null;
    /**
     * A cached copy of the current column (for JTable implementations).
     */
    protected int m_iThisColumn = -1;
    /**
     * A cached copy of the current row (for JTable implementations).
     */
    protected int m_iThisRow = -1;
    /**
     * All the icons.
     */
    protected ImageIcon m_rgIcons[] = null;
    /**
     * The timer.
     */
    private javax.swing.Timer m_timer = null;
    /**
     * The current icon displayed.
     */
    private int m_iCurrentIcon = 0;

    /**
     * Creates new JBlinkLabel.
     */
    public JBlinkLabel()
    {
        super();
    }
    /**
     * Creates new JBlinkLabel.
     * @param obj Undefined.
     */
    public JBlinkLabel(Object obj)
    {
        this();
        this.init(obj);
    }
    /**
     * Creates new JBlinkLabel.
     * @param obj Undefined.
     */
    public void init(Object obj)
    {
        for (int i = 0; i < MAX_ICONS; i++)
        {
            ImageIcon icon = this.getImageIcon(i);
            if (icon == null)
                break;
            this.addIcon(icon, i);
        }
    }
    /**
     * Get rid of the resources and remove this component.
     */
    public void free()
    {
        if (m_timer != null)
            this.removeTimer();
    }
    /**
     * If this control is in a JTable, this is how to render it.
     * @param table The table this component is in.
     * @param value The value of this cell.
     * @param isSelected True if selected.
     * @param hasFocus True if focused.
     * @param row The table row.
     * @param column The table column.
     * @return This component (after updating for blink).
     */
    public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (m_table == null)
        {   // Cache this for later.
            m_table = table;
            // The following code is here because the column of this component is sometimes different from the column in the model:
            TableColumnModel columnModel = table.getColumnModel();
            for (int iColumn = 0; iColumn < columnModel.getColumnCount(); iColumn++)
            {
	            TableColumn tableColumn = columnModel.getColumn(iColumn);
	            TableCellRenderer renderer = tableColumn.getCellRenderer();
	            if (renderer == this)
	            	m_iThisColumn = column;
            }
        }
        m_iThisRow = row;
        ImageIcon icon = this.getImageIcon(value);
        this.setIcon(icon);
        return this;
    }
    /**
     * Here is the value of this object, display the correct image.
     * @return The appropriate image.
     */
    public ImageIcon getImageIcon(Object value)
    {
        if (value == null)
            return m_rgIcons[0];
        String strType = value.toString();
        int iType = 0;
        try   {
            iType = Integer.parseInt(strType);
        } catch (NumberFormatException ex)  {
        }

        if (m_rgIcons == null)
        	return null;
        int iIconCount = 0;
        for (int i = 1; i < MAX_ICONS; i++)
        {
            if (m_rgIcons[i] != null)
                if (((1 << i) & iType) != 0)    // Only count icons in this type
                    iIconCount++;
        }
        if (iIconCount == 0)
            iIconCount = 1;
        int iRelIndex;
        if ((iType & 1) == 0)
        {   // Cycle through all the icons
            iRelIndex = m_iCurrentIcon % iIconCount;    // Remainder of division
        }
        else
        {   // Alternate from 0, next, 0, next, etc
            if ((m_iCurrentIcon & 1) == 0)
                iRelIndex = MAX_ICONS;      // Icon 0
            else
            {
                int iIconIndex = m_iCurrentIcon / 2 + 1;
                iRelIndex = iIconIndex % iIconCount;    // Remainder of division
            }
        }
        int i;
        for (i = 1; i < MAX_ICONS; i++)
        {
            if (m_rgIcons[i] != null)
                if (((1 << i) & iType) != 0)    // Only count icons in this type
                    iRelIndex--;
            if (iRelIndex < 0)
                break;
        }
        if (i >= MAX_ICONS)
            i = 0;

        return m_rgIcons[i];
    }
    /**
     * Add this icon to the list of icons alternating for this label.
     * @param icon The icon to add.
     * @param iIndex The index for this icon.
     */
    public void addIcon(ImageIcon icon, int iIndex)
    {
        if (m_rgIcons == null)
        {
            m_rgIcons = new ImageIcon[MAX_ICONS];
            m_timer = new javax.swing.Timer(500, this);         // Remind me to change graphics every 1/2 second
            m_timer.start();
        }
        if (iIndex < MAX_ICONS)
            m_rgIcons[iIndex] = icon;
    }
    /**
     * Remove this icon from the list of icons alternating for this label.
     * @param iIndex The index for this icon.
     */
    public void removeIcon(int iIndex)
    {
        if (m_rgIcons != null)
        {
            m_rgIcons[iIndex] = null; // Always at location 0
            for (int i = 0; i < MAX_ICONS; i++)
            {
                if (m_rgIcons[i] != null)
                    return;
            }
            this.removeTimer();   // No icons left
            m_rgIcons = null;
        }
    }
    /**
     * 0.5 seconds passed, select the item.
     * <p>NOTE: This method does not cycle through the icons, it flashes the START_ICON, then the next
     * icon, then the start again, then the next one in the list.
     * <p>ie., If the count is even, display icon 0; if not, display (icon / 2 + 1).
     * @param e The ActionEvent.
     */
    public void actionPerformed(ActionEvent e)
    {
        m_iCurrentIcon++;
        if (e.getSource() instanceof javax.swing.Timer)
        {
            if (m_table != null)
            {
                m_table.repaint();      // todo(don) May want to just invalidate the column with this icon in it
            }
            else
            {
                Object objValue = this.getName(); // This is where the value is cached
                ImageIcon icon = this.getImageIcon(objValue);
                if (icon != null)
                {
                    this.setIcon(icon);
                    Rectangle rect = this.getBounds();
                    this.repaint(rect);
                }
            }
        }
    }
    /**
     * Remove this timer.
     */
    public synchronized void removeTimer()
    {
        if (m_timer != null)
        {
            m_timer.stop();
            m_timer.removeActionListener(this);
            m_timer = null;
        }
    }
    /**
     * Override this method to supply the icons for this Label (if you don't manually add them).
     * For example:
     * <pre>
     *      String strType = "Hotel";
     *  {
     *      if (iType == 0)
     *          strType = "Item";
     *      if (iType == 1)
     *          strType = "Hotel";
     *  }
     *  ClassLoader cl = this.getClass().getClassLoader(); 
     *  // Create icons 
     *  URL url = cl.getResource("images/tour/buttons/" + strType + ".gif");
     *  return new ImageIcon(url);
     * </pre>
     */
    public ImageIcon getImageIcon(int iType)
    {
        return null;
    }
    /**
     * Get the value (On, Off or Null).
     * @return The raw data (an Integer).
     */
    public Object getControlValue()
    {
        String strValue = this.getName();
        int iValue = 0;
        try   {
            iValue = Integer.parseInt(strValue);
        } catch (NumberFormatException ex)  {
        }
        return new Integer(iValue);
    }
    public static final String ZERO = "0";
    /**
     * Set the value.
     * @param objValue The raw data (an Integer).
     */
    public void setControlValue(Object objValue)
    {
        String strValue = ZERO;
        if (objValue instanceof Integer)
            strValue = objValue.toString();
        this.setName(strValue);
    }
    /**
     * Get the converter for this screen field.
     * @return The converter for this screen field.
     */
    public Converter getConverter()
    {
        return null;
    }
}
