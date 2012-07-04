/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.field.convert;

/**
 * @(#)MultipleFieldConverter.java  0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */

import java.util.Vector;

import org.jbundle.model.db.Convert;
import org.jbundle.model.screen.ScreenComponent;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.thin.base.db.LinkedConverter;


/**
 * This is a base converter class that is set up to keep track of several target converter.
 * Usually, you just override the getIndexOfConverterToPass() method and return the
 * index of the converter to as the current converter.
 *
 * @version 1.0.0
 * @author    Don Corley
 */
public class MultipleFieldConverter extends FieldConverter
{
    /**
     * List of converters.
     */
    protected Vector<Converter> m_vconvDependent = null;
    /**
     * Enable or disable the converter translation.
     */
    protected boolean m_bEnableTranslation = true;

    /**
     * Constructor.
     */
    public MultipleFieldConverter()
    {
        super();
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     */
    public MultipleFieldConverter(Converter converter)
    {
        this();
        this.init(converter, null);
    }
    /**
     * Constructor.
     * @param converter The next converter in the converter chain.
     * @param converterAlt The first converter on this list of alternates.
     */
    public MultipleFieldConverter(Converter converter, Converter converterAlt)
    {
        this();
        this.init(converter, converterAlt);
    }
    /**
     * Initialize this converter.
     * @param converter The next converter in the converter chain.
     * @param converterAlt The first converter on this list of alternates.
     */
    public void init(Converter converter, Converter converterAlt)
    {
        m_bEnableTranslation = true;
        m_bSetData = false;
        super.init(converter);
        if (converterAlt != null)
            this.addConverterToPass(converterAlt);
    }
    /**
     * Free this converter.
     */
    public void free()
    {
        super.free();
        m_vconvDependent = null;
    }
    /**
     * Add this component to the components displaying this field.
     * Make sure all the converter have this screenfield on their list.
     * @param Object sField The screen component.. either a awt.Component or a ScreenField.
     */
    public void addComponent(Object screenField)
    { // Set up the dependencies, This will recompute if any change from these three fields
        this.setEnableTranslation(false);
        super.addComponent(screenField);
        this.setEnableTranslation(true);
        for (int iIndex = 0; ; iIndex++)
        {
            Converter converter = this.getConverterToPass(iIndex);
            if (converter == null)
                break;
            converter.addComponent(screenField);
        }
    }
    /**
     * Remove this control from this field's control list.
     * @param Object sField The screen component.. either a awt.Component or a ScreenField
     */
    public void removeComponent(Object screenField)
    { // Set up the dependencies, This will recompute if any change from these three fields
        for (int iIndex = 0; ; iIndex++)
        {
            Converter converter = this.getConverterToPass(iIndex);
            if (converter == null)
                break;
            converter.removeComponent(screenField);
        }
        this.setEnableTranslation(false);
        super.removeComponent(screenField);
        this.setEnableTranslation(true);
    }
    /**
     * For binary fields, set the current state.
     * @param state The state to set this field.
     * @param bDisplayOption Display changed fields if true.
     * @param iMoveMode The move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setData(Object state, boolean bDisplayOption, int iMoveMode)
    {       // Must be overidden
        m_bSetData = true;  // Make sure getNextConverter is called correctly (if it is called).
        int iErrorCode = super.setData(state, bDisplayOption, iMoveMode);
        m_bSetData = false;
        return iErrorCode;
    }
    /**
     * For binary fields, set the current state.
     * @param state The state to set this field.
     * @param bDisplayOption Display changed fields if true.
     * @param iMoveMode The move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setState(boolean state, boolean bDisplayOption, int iMoveMode)
    {       // Must be overidden
        m_bSetData = true;  // Make sure getNextConverter is called correctly (if it is called).
        int iErrorCode = super.setState(state, bDisplayOption, iMoveMode);
        m_bSetData = false;
        return iErrorCode;
    }
    /**
     * Convert and move string to this field.
     * Override this method to convert the String to the actual Physical Data Type.
     * @param strString the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code (or NORMAL_RETURN).
     */
    public int setString(String strString, boolean bDisplayOption, int iMoveMode)    // init this field override for other value
    {
        m_bSetData = true;  // Make sure getNextConverter is called correctly (if it is called).
        int iErrorCode = super.setString(strString, bDisplayOption, iMoveMode);
        m_bSetData = false;
        return iErrorCode;
    }
    /**
     * For numeric fields, set the current value.
     * Override this method to convert the value to the actual Physical Data Type.
     * @param bState the state to set the data to.
     * @param bDisplayOption Display the data on the screen if true.
     * @param iMoveMode INIT, SCREEN, or READ move mode.
     * @return The error code.
     */
    public int setValue(double value, boolean bDisplayOption, int iMoveMode)
    {
        m_bSetData = true;  // Make sure getNextConverter is called correctly (if it is called).
        int iErrorCode = super.setValue(value, bDisplayOption, iMoveMode);
        m_bSetData = false;
        return iErrorCode;
    }
    /**
     * Am I retrieving or setting data?
     */
    protected boolean m_bSetData = false;
    /**
     * Get the next Converter in the chain.
     */
    public Converter getNextConverter() 
    {
        if ((m_bEnableTranslation == true) && (this.getConverterToPass(m_bSetData) != null))
            return this.getConverterToPass(m_bSetData); // Retrieve the dependent field
        else
            return super.getNextConverter();   // Retrieve this info
    }
    /**
     * Get the index of the converter to use (-1 means use the next converter on the chain).
     * Should I pass the alternate field (or the main field)?
     * Override this method.
     * @param bSetData If true I will be set(ing) the data of this field, if false I will be get(ing) the data.
     * @return index (-1)= next converter, 0 - n = List of converters.
     */
    public int getIndexOfConverterToPass(boolean bSetData)
    {
        return -1;      // Use the next converter on the chain
    } // Override this
    /**
     * Get the current converter to use in the converter calls.
     * @param bSetData If true I will be set(ing) the data of this field, if false I will be get(ing) the data.
     * @return The current converter.
     */
    public Converter getConverterToPass(boolean bSetData)
    {
        return this.getConverterToPass(this.getIndexOfConverterToPass(bSetData));
    }
    /**
     * Get this converter to use at this index.
     * @param iIndex The converter index.
     * @return The current converter.
     */
    public Converter getConverterToPass(int iIndex)
    {
        if (iIndex == -1)
            return super.getNextConverter();
        if (m_vconvDependent != null)
        {
            if (iIndex < m_vconvDependent.size())
                return (Converter)m_vconvDependent.get(iIndex);
        }
        return null;        // Use the next converter on the chain
    }
    /**
     * Add this converter to pass.
     * @param converter Add this converter to the list.
     */
    public void addConverterToPass(Converter converter)
    {
        if (m_vconvDependent == null)
            m_vconvDependent = new Vector<Converter>();
        m_vconvDependent.add(converter);
        this.setEnableTranslation(false);
        if ((this.getNextConverter() == null)
            || (this.getNextConverter().getField() == null))
        {
        this.setEnableTranslation(true);
            return;
        }
        for (int iSeq = 0; ; iSeq++)
        {
            ScreenComponent sField = (ScreenComponent)this.getNextConverter().getField().getComponent(iSeq);
            if (sField == null)
                break;
            if (!this.isConverterInPath(sField))
                continue;  // This converter does not belong to this screen field
            if (converter != null)
                if (converter.getField() != null)
            {
                boolean bFound = false;
                for (int iSeq2 = 0; ; iSeq2++)
                {
                    if (converter.getField().getComponent(iSeq2) == null)
                        break;
                    if (converter.getField().getComponent(iSeq2) == sField)
                        bFound = true;
                }
                if (!bFound)
                    converter.addComponent(sField);
            }
        }
        this.setEnableTranslation(true);
    }
    /**
     * Enable or disable the converter translation.
     * @param bEnableTranslation If true, enable translation.
     * @return The old translation value.
     */
    public boolean setEnableTranslation(boolean bEnableTranslation)
    {
        boolean bOldTranslation = m_bEnableTranslation;
        m_bEnableTranslation = false;
        LinkedConverter converter = this;
        while (converter != null)
        {
            if (converter.getNextConverter() instanceof LinkedConverter)
            {
                converter = (LinkedConverter)((LinkedConverter)converter).getNextConverter();
                if (converter instanceof MultipleFieldConverter)
                    ((MultipleFieldConverter)converter).setEnableTranslation(bEnableTranslation);
            }
            else
                converter = null;
        }
        m_bEnableTranslation = bEnableTranslation;
        return bOldTranslation;
    }
    /**
     * Is this converter in the converter path of this screen field.
     * @return true if it is.
     */
    public boolean isConverterInPath(ScreenComponent sField)
    {
        Convert converter = sField.getConverter();
        while (converter != null)
        {
            if (converter == this)
                return true;
            if (converter instanceof LinkedConverter)
            {
                MultipleFieldConverter convMultiple = null;
                boolean bOldEnable = false;
                if (converter instanceof MultipleFieldConverter)
                    convMultiple = (MultipleFieldConverter)converter;
                if (convMultiple != null)
                    bOldEnable = convMultiple.setEnableTranslation(false);
                converter = ((LinkedConverter)converter).getNextConverter();
                if (convMultiple != null)
                    convMultiple.setEnableTranslation(bOldEnable);
            }
            else
                converter = null;
        }
        return false;   // Not found
    }
}
