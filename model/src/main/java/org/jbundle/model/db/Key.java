/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.model.db;

import org.jbundle.model.Freeable;

/**
 * @(#)KeyArea.java   1.16 95/12/14 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */


/**
 * A KeyArea describes a particular key area (fields and order).
 * KeyArea - Definition of this key area for the thin implementation.
 * Be very careful when using this implementation, because the internal
 * data representation is much different than a KeyArea (in the thick model).
 * In this (thin) model the m_vKeyFieldList is used to save the actual fields,
 * where in the thick model this field is used to store KeyField(s).
 * Also, since there is not a KeyField to store temporary data, there is a
 * tempDataArea to store the field values (see compareKeys() and setupKeyBuffer()).
 *
 * @version 1.0.0
 *
 * @author Don Corley
 */
public interface Key
    extends Freeable
{
    /**
     * Initialize the class.
     * @param record The parent record.
     * @param iKeyDup The type of key (UNIQUE/NOT_UNIQUE/SECONDARY).
     * @param strKeyName The name of this key (default to first fieldnameKEY).
     */
    public void init(Rec record, int iKeyDup, String strKeyName);
    /**
     * Add this field to this Key Area.
     * @param iFieldSeq The field to add.
     * @param bKeyArea The order (ascending/descending).
     */
    public void addKeyField(int iFieldSeq, boolean bKeyOrder);
    /**
     * Add this field to this Key Area.
     * @param strFieldName The field to add.
     * @param bKeyArea The order (ascending/descending).
     */
    public void addKeyField(String strFieldName, boolean bKeyOrder);
    /**
     * Add this field to this Key Area.
     * @param field The field to add.
     * @param bKeyArea The order (ascending/descending).
     */
    public void addKeyField(Field field, boolean bKeyOrder);
    /**
     * Get the Field in this KeyField.
     * @param iKeyFieldSeq The position of this field in the key area.
     * @return The field.
     */
    public Field getField(int iKeyFieldSeq);
    /**
     * Key area count.
     * @return key field count.
     */
    public int getKeyFields();
    /**
     * Get the adjusted field count.
     * Add one key field (the counter field if this isn't a unique key area and you want to force a unique key).
     * @param bForceUniqueKey If params must be unique, if they aren't, add the unique key to the end.
     * @param bIncludeTempFields If true, include any temporary key fields that have been added to the end if this keyarea
     * @return The Key field count.
     */
    public int getKeyFields(boolean bForceUniqueKey, boolean bIncludeTempFields);
    /**
     * Get the key name.
     * @return The key name for this key area.
     */
    public String getKeyName();
    /**
     * Get the key order for this key field (Ascending/Descending).
     * Note: This is not implemented for thin.
     * @param iKeyFieldSeq The field to check.
     * @return true if ascending order.
     */
    public boolean getKeyOrder(int keyFieldSeq);
    /**
     * Get the parent record.
     * @return The parent record.
     */
    public Rec getRecord();
    /**
     * Is this a unique key?
     * @return The unique key code (Unique/Not unique/secondary).
     */
    public int getUniqueKeyCode();
    /**
     * Initialize the Key Fields.
     * @param iAreaDesc The (optional) temporary area to copy the current fields to.
     * @see BaseField.zeroKeyFields(int).
     */
    public void zeroKeyFields(int iAreaDesc);
    /**
     * Compare these two keys and return the compare result.
     * <p />Note: The thin implementation is completely different from the thick implementation
     * here, the areadesc is ignored and the thin data data area is compared an returned.
     * @param iAreaDesc (Always ignored for the thin implementation).
     * @return The compare result (see compareTo method).
     */
    public int compareKeys(int iAreaDesc);
    /**
     * Order the keys ascending or descending?
     */
    public void setKeyOrder(boolean bKeyOrder);
    /**
     * Order the keys ascending or descending?
     * NOTE: Only used in Thin.
     */
    public boolean getKeyOrder();
}
