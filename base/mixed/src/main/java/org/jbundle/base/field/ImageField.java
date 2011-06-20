package org.jbundle.base.field;


import java.io.Serializable;

import javax.swing.ImageIcon;

import org.jbundle.base.db.Record;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.SCannedBox;
import org.jbundle.base.screen.model.SImageView;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.screen.util.SerializableImage;


/**
 * ImageField - A GIF Picture.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class ImageField extends ObjectField
{
	private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public ImageField()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public ImageField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Initialize the object.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Creates a new object of the same class as this object.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        BaseField field = new ImageField(null, m_strFieldName, m_iMaxLength, m_strFieldDesc, null);
        field.setRecord(m_record);     // Set table without adding to table field list
        return field;
    }
    /**
     * Set up the default screen control for this field.
     * @param itsLocation Location of this component on screen (ie., GridBagConstraint).
     * @param targetScreen Where to place this component (ie., Parent screen or GridBagLayout).
     * @param converter The converter to set the screenfield to.
     * @param iDisplayFieldDesc Display the label? (optional).
     * @return Return the component or ScreenField that is created for this field.
     */
    public ScreenField setupDefaultView(ScreenLocation itsLocation, BasePanel targetScreen, Converter converter, int iDisplayFieldDesc)   // Add this view to the list
    {
        SImageView screenField = new SImageView(itsLocation, targetScreen, converter, iDisplayFieldDesc);
        ScreenField pSScreenField = new SCannedBox(targetScreen.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), targetScreen, converter, SCannedBox.OPEN, ScreenConstants.DONT_DISPLAY_FIELD_DESC, this);
        pSScreenField.setRequestFocusEnabled(false);
        return screenField;
    }
    /**
     * Get this image as an icon.
     */
    public ImageIcon getImage()
    {
        if (this.isNull())
            return null;
        if (this.getData() instanceof ImageIcon)
            return (ImageIcon)this.getData();   // Never (legacy)
        else if (this.getData() instanceof SerializableImage)
            return new ImageIcon(((SerializableImage)this.getData()).getImage());
        else
            return null;
    }
} 
