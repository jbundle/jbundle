/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)FieldConverter.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.Map;

import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.ScreenModel;
import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ComponentParent;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.model.screen.ScreenLoc;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.screen.util.LinkedConverter;


/**
 * The base converter for fields.
 * The class maintains the converter chain.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class FieldConverter extends LinkedConverter
{
    /**
     * Constructor.
     */
    public FieldConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     */
    public FieldConverter(Converter converter)
    {
        this();
        this.init(converter);
    }
    /**
     * Initialize this converter.
     * @param converter The next converter in the converter chain.
     */
    public void init(Converter converter)
    {
        super.init(converter);
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Set up the default control for this field.
     *  @param  itsLocation     Location of this component on screen (ie., GridBagConstraint).
     * @param  targetScreen    Where to place this component (ie., Parent screen or GridBagLayout).
     * @param  iDisplayFieldDesc Display the label? (optional).
     *  @return   Return the component or ScreenField that is created for this field.
     */
    public ScreenComponent setupDefaultView(ScreenLoc itsLocation, ComponentParent targetScreen, Convert converter, int iDisplayFieldDesc, Map<String, Object> properties)   // Add this view to the list
    {
        ScreenComponent sField = null;
        BaseField field = (BaseField)this.getField();
        if (field != null)
        {
            sField = field.setupDefaultView(itsLocation, targetScreen, converter, iDisplayFieldDesc, properties);
            if (sField != null) if (sField.getConverter() == null)
                sField.setConverter(this);
        }
        else
            sField = BaseField.createScreenComponent(ScreenModel.EDIT_TEXT, itsLocation, targetScreen, converter, iDisplayFieldDesc, properties);
        return sField;
    }
}
