package org.jbundle.base.field.convert;

/**
 * @(#)FieldConverter.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import org.jbundle.base.field.BaseField;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.SEditText;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.model.Convert;
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
     *  @param  targetScreen    Where to place this component (ie., Parent screen or GridBagLayout).
     *  @param  iDisplayFieldDesc Display the label? (optional).
     *  @return   Return the component or ScreenField that is created for this field.
     */
    public ScreenField setupDefaultView(ScreenLocation itsLocation, BasePanel targetScreen, int iDisplayFieldDesc)  // Add this view to the list
    {
        return this.setupDefaultView(itsLocation, targetScreen, this, iDisplayFieldDesc);
    }
    /**
     * Set up the default control for this field.
     *  @param  itsLocation     Location of this component on screen (ie., GridBagConstraint).
     *  @param  targetScreen    Where to place this component (ie., Parent screen or GridBagLayout).
     *  @param  iDisplayFieldDesc Display the label? (optional).
     *  @return   Return the component or ScreenField that is created for this field.
     */
    public Object setupDefaultView(Object itsLocation, Object targetScreen, Convert converter, int iDisplayFieldDesc)   // Add this view to the list
    {   // I NEVER call the thin implementation.
        return this.setupDefaultView((ScreenLocation)itsLocation, (BasePanel)targetScreen, (Converter)converter, iDisplayFieldDesc);
    }
    /**
     * Set up the default control for this field.
     *  @param  itsLocation     Location of this component on screen (ie., GridBagConstraint).
     *  @param  targetScreen    Where to place this component (ie., Parent screen or GridBagLayout).
     *  @param  iDisplayFieldDesc Display the label? (optional).
     *  @return   Return the component or ScreenField that is created for this field.
     */
    public ScreenField setupDefaultView(ScreenLocation itsLocation, BasePanel targetScreen, Converter converter, int iDisplayFieldDesc)   // Add this view to the list
    {
        ScreenField sField = null;
        BaseField field = (BaseField)this.getField();
        if (field != null)
        {
            sField = field.setupDefaultView(itsLocation, targetScreen, converter, iDisplayFieldDesc);
            if (sField != null) if (sField.getConverter() == null)
                sField.setConverter(this);
        }
        else
            sField = new SEditText(itsLocation, targetScreen, converter, iDisplayFieldDesc);
        return sField;
    }
}
