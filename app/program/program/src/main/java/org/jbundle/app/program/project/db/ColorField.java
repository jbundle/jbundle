/**
 * @(#)ColorField.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.project.db;

import java.awt.Color;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.IntegerField;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.model.util.Colors;

/**
 *  ColorField - .
 */
public class ColorField extends IntegerField
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Default constructor.
     */
    public ColorField()
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
    public ColorField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Get the color.
     */
    public int getColor()
    {
        if (this.isNull())
            return Colors.NULL;
        return ((Integer)this.getData()).intValue();
    }
    /**
     * SetColor Method.
     */
    public int setColor(Color color, boolean bDisplayOption, int iMoveMode)
    {
        Integer intColor = null;
        if (color != null)
            intColor = new Integer(color.getRGB());
        return this.setData(intColor, bDisplayOption, iMoveMode);
    }
    /**
     * SetColor Method.
     */
    public int setColor(Color color)
    {
        return this.setColor(color, true, DBConstants.SCREEN_MOVE);
    }
    /**
     * Set up the default screen control for this field.
     * @param itsLocation Location of this component on screen (ie., GridBagConstraint).
     * @param targetScreen Where to place this component (ie., Parent screen or GridBagLayout).
     * @param converter The converter to set the screenfield to.
     * @param iDisplayFieldDesc Display the label? (optional).
     * @param properties Extra properties
     * @return Return the component or ScreenField that is created for this field.
     */
    public ScreenComponent setupDefaultView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert converter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        return super.setupDefaultView(itsLocation, targetScreen, converter, iDisplayFieldDesc, properties);   // FIX THIS
    }

}
