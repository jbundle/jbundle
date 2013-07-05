/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db;

/**
 * @(#)KeyField.java    1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import org.jbundle.base.field.BaseField;
import org.jbundle.base.model.DBConstants;

/**
 * KeyField - Definition of this key field.
 */
public class KeyField extends Object
{
    /**
     * The key area that owns this keyfield.
     */
    protected KeyArea m_keyArea = null;
    /**
     * The order of this key field (ASCENDING/DESCENDING).
     */
    protected boolean m_bOrder = DBConstants.ASCENDING;      // Key field order
    /**
     * The key field.
     */
    protected BaseField m_field = null;     // BaseField
    /**
     * The place to store the TEMP_KEY_AREA. The temp key area is typically used to
     * store the last read key, so if the user messes up the data record, you still
     * have the correct key for an update or delete.
     */
    protected BaseField m_fieldTempParam = null;
    /**
     * The place to store the START_KEY_AREA.
     */
    protected BaseField m_fieldStartParam = null;
    /**
     * The place to store the END_KEY_AREA.
     */
    protected BaseField m_fieldEndParam = null;
    /**
     * If this is a temporary key field.
     */
    protected boolean m_bIsTemporary = false;

    /**
     * Constructor
     */
    public KeyField()
    {
        super();
    }
    /**
     * Constructor.
     * @param keyArea The parent key area.
     * @param field The key field.
     * @param keyOrder Ascending or Descending.
     */
    public KeyField(KeyArea keyArea, BaseField field, boolean keyOrder)
    {
        this();
        this.init(keyArea, field, keyOrder);
    }
    /**
     * Constructor.
     * @param keyArea The parent key area.
     * @param field The key field.
     * @param keyOrder Ascending or Descending.
     */
    public void init(KeyArea keyArea, BaseField field, boolean keyOrder)
    {
        this.m_keyArea = keyArea;
        this.m_field = field;
        this.m_bOrder = keyOrder;
        m_bIsTemporary = false;
        m_keyArea.addKeyField(this);
    }
    /**
     * Free this key field.
     */
    public void free()
    {
        m_keyArea.removeKeyField(this);
        m_keyArea = null;
        m_field = null;
        if (m_fieldTempParam != null)
            m_fieldTempParam.free();
        m_fieldTempParam = null;        // BaseField
        if (m_fieldStartParam != null)
            m_fieldStartParam.free();
        m_fieldStartParam = null;   // BaseField
        if (m_fieldEndParam != null)
            m_fieldEndParam.free();
        m_fieldEndParam = null;         // BaseField
    }
    /**
     * Get the field that this KeyField points to.
     * @param areaDesc KeyArea type File/Temp/Start/End.
     * @return The field.
     */
    public BaseField getField(int iAreaDesc)
    {
        switch (iAreaDesc)
        {
        default:
        case DBConstants.FILE_KEY_AREA:
            return m_field;
        case DBConstants.TEMP_KEY_AREA:
            if (m_fieldTempParam == null)
            {
                try   {
                    m_fieldTempParam = (BaseField)m_field.clone();  // Buffer areas
                    m_fieldTempParam.setFieldName(new String(m_field.getFieldName(false, false) + "Temp"));
                    m_fieldTempParam.setNullable(true);
                } catch (CloneNotSupportedException ex)   {
                    m_fieldTempParam = null;
                }
            }
            return m_fieldTempParam;
        case DBConstants.START_SELECT_KEY:
            if (m_fieldStartParam == null)
            {
                try   {
                    m_fieldStartParam = (BaseField)m_field.clone();   // Buffer areas
                    m_fieldStartParam.setFieldName(new String(m_field.getFieldName(false, false) + "Start"));
                    m_fieldStartParam.setNullable(true);
                } catch (CloneNotSupportedException ex)   {
                    m_fieldStartParam = null;
                }
            }
            return m_fieldStartParam;
        case DBConstants.END_SELECT_KEY:
            if (m_fieldEndParam == null)
            {
                try   {
                    m_fieldEndParam = (BaseField)m_field.clone();
                    m_fieldEndParam.setFieldName(new String(m_field.getFieldName(false, false) + "End"));
                    m_fieldEndParam.setNullable(true);
                } catch (CloneNotSupportedException ex)   {
                    m_fieldEndParam = null;
                }
            }
            return m_fieldEndParam;
        }
    }
    /**
     * Ascending or Descending.
     * @return The key order.
     */
    public boolean getKeyOrder()
    {
        return m_bOrder;
    }
    /**
     * Ascending or Descending.
     * @param bOrder The new order.
     */
    public void setKeyOrder(boolean bOrder)
    {
        m_bOrder = bOrder;
    }
    /**
     * If this is a temporary key field.
     * @return
     */
    public boolean isTemporary()
    {
        return m_bIsTemporary;
    }
    /**
     * If this is a temporary key field.
     * @return
     */
    public void setIsTemporary(boolean bIsTemporary)
    {
        m_bIsTemporary = bIsTemporary;
    }
}
