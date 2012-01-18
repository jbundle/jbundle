/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.model.db;

import java.util.Map;

import org.jbundle.model.DBException;
import org.jbundle.model.Freeable;
import org.jbundle.model.Task;
import org.jbundle.model.screen.ComponentParent;

/**
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
/**
 * FieldList - Collection of field information.
 * This is the thin version of a record.
 */
public interface Rec
    extends Freeable
{
    public static final String ID = "ID";
    public static final String LAST_CHANGED = "LastChanged";
    public static final String DELETED = "Deleted";

    public static final String ID_KEY = "ID";

    /**
     * init.
     */
    public void init(Object recordOwner);
    /**
     * Make an exact copy of this FieldList.
     * @return     a clone of this instance.
     * @exception  CloneNotSupportedException  if the object's class does not support the <code>Cloneable</code> interface.
     * @see        java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException;
    /*
     * Get the "record" owner.
     */
    public Object getOwner();
    /**
     * Get the "record" owner.
     */
    public void setOwner(Object recordOwner);
    /**
     * Create a default document for file maintenance or file display.
     * Usually overidden in the file's record class.
     * @param itsLocation The location of the screen in the parentScreen (usually null here).
     * @param parentScreen The parent screen.
     * @param iDocMode The type of screen to create (MAINT/DISPLAY/SELECT/MENU/etc).
     * @return The new screen.
     */
    public ComponentParent makeScreen(Object itsLocation, ComponentParent parentScreen, int iDocMode, Map<String, Object> properties);
    /**
     * Get the name of this table.
     * Override this to supply the name of the table.
     * Note: This is almost always overidden (except for mapped files)
     * @param bAddQuotes if the table name contains spaces, add quotes.
     * @return The name of this table.
     */
    public String getTableNames(boolean bAddQuotes);
    /**
     * Set the table name.
     */
    public void setTableNames(String tableName);
    /**
     * Get the remote class name.
     * Just remove thin from this class name!.
     * @return The full remote class name.
     */
    public String getRemoteClassName();
    /**
     *  Get the Database Name.
     * @return The database name. This is supplied in the overidden class.
     */
    public /*?abstract*/ String getDatabaseName();
    /**
     *  Is this a local (vs remote) file?.
     * @return The database type.
     * LOCAL - Read-only local (or copy of remote) table.
     * REMOTE - Remote table.
     * SCREEN - Screen table.
     * VECTOR - Vector database where same records share one table
     * UNSHAREABLE_VECTOR - Vector database where all tables are new
     */
    public /**?abstract*/ int getDatabaseType();
    /**
     * Get the table for this record.
     * @return The table for this record.
     */
    public Table getTable();
    /**
     * Set the table for this record.
     * @param table The table for this record.
     */
    public void setTable(Table table);
    /**
     * Add this field to this record.
     * @param field A field to add to the end of the current fieldlist.
     */
    public void addField(Field field);
    /**
     * Number of Fields in this record.
     * @return The current field count.
     */
    public int size();
    /**
     * Get rid of this method.
     * @return The current field count.
     */
    public int getFieldCount();
    /**
     * Get this field in the record.
     * @param iFieldSeq The field position to retrieve.
     * @return The field at this position (Or null if past end of records).
     */
    public Field getField(int iFieldSeq);
    /**
     * Get this field in the record.
     * @param strFieldName The field name to find.
     * @return The field at this position (Or null if past end of records).
     */
    public Field getField(String strFieldName);
    /**
     * Remove this field from this record.
     * @param field Field to remove.
     * @return true if removed.
     */
    public boolean removeField(Field field);
    /**
     * Convert this record to a string.
     * @return A string of all the fields.
     */
    public String toString();
    /**
     * Reset all the fields to their default value.
     * <p />NOTE: After executing, this records edit mode is set to NONE.
     * @param bDisplay The the new values (on work in the thick model).
     * @throws Exception An exception on initing a field.
     */
    public void initRecord(boolean bDisplay) throws DBException;
    /**
     * Called each time a field changed.
     * This method replaces behaviors in the thick model. Just override this method and look for the
     * target field and do the listener associated with a change of value.
     * Note: The thin version (this one) keeps a flag for changes, the thick version surveys the fields.
     * @param field The field that changed.
     * @param iChangeType The type of change (See record for the list).
     * @param iDisplayOtion The display option.
     * @return The error code.
     */
    public int doRecordChange(Field field, int iChangeType, boolean bDisplayOption);
    /**
     * The addPropertyChangeListener method was generated to support the propertyChange field.
     * @param listener The property change listener to add to my listeners.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener listener);
    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     * @param propertyName The property name.
     * @param oldValue The old value.
     * @param newValue The new value.
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue);
    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     * @param listener The property change listener to remove from my listeners.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener listener);
    /**
     * Property change support.
     */
    /**
     * Have any fields Changed?
     * Note: The thin version (this one) keeps a flag for changes, the thick version surveys the fields.
     * @return true if any fields have changed.
     */
    public boolean isModified();
    /**
     * Get the current edit mode for this record.
     * @return The edit mode.
     * <pre>
     * EDIT_NONE - Unknown
     * EDIT_ADD - New (unwritten) record.
     * EDIT_IN_PROGRESS - Current record in Edit mode (locked).
     * EDIT_CURRENT - Current record (read from table).
     * </pre>
     */
    public int getEditMode();
    /**
     * Set the current edit mode for this record.
     * @param dbEditMode The edit mode.
     * @return Old edit mode.
     * @see getEditMode.
     */
    public int setEditMode(int dbEditMode);
    /**
     * Set the current open mode for this record.
     * Set this before you call Open.
     * @param dbOpenMode The current open mode.
     * @return The edit mode.
     * @see getOpenMode
     */
    public int setOpenMode(int dbOpenMode);
    /**
     * Get the current open mode for this record.
     * @return The current open mode.
     * <pre>
     * dbOpenNone - Lock on edit, allow deletes and changes.
     * dbOpenReadOnly - No deletes and changes.
     * dbOpenAppendOnly - No deletes and changes, adds only.
     * dbRefreshAndLockOnChange - If the record is new, refreshes the fields before the first field change. (For counter fields).
     * </pre>
     */
    public int getOpenMode();
    /**
     * The resource bundle that contains the values for this record's fields.
     */
    /**
     * Get the string that matches this key.
     * This method traverses the class hierarchy for a matching string that matches this key value.
     * If the resource bundle hasn't been read yet, reads the bundle from the .res package.
     * @param strResource The key.
     * @return The value (returns the key if no value is found).
     */
    public String getString(String strResource);
    /**
     * Get the task for this field list.
     * This is a convience method which calls this.getRecordOwner().getTask() in the thick model.
     * Note: This is always the shared instance of BaseApplet for the thin model.
     * @return The task which contains this fieldList's recordowner.
     */
    public Task getTask();
    /**
     * Get the name of the current key.
     * Note: This is a convenience method, the current KeyArea's name is returned.
     * @return The key's name.
     */
    public String getKeyName();
    /**
     * Get the name of the current key.
     * Note: This is a convenience method, this is the same as setKeyArea (with no return).
     * @param strKeyName The key's name.
     */
    public void setKeyName(String strKeyName);
    /**
     * Set the default key order.
     * @param String strKeyName the current index.
     * @return The new default key area (null if not found).
     */
    public Key setKeyArea(String strKeyName);
    /**
     * Add this key area to this record.
     * @param keyArea KeyArea to add.
     */
    public void addKeyArea(Key keyArea);
    /**
     * Remove this key area from this record.
     * @param keyArea KeyArea to remove.
     * @return true if successful.
     */
    public boolean removeKeyArea(Key keyArea);
    /**
     * Get the key order.
     * @return The current Key order.
     */
    public int getDefaultOrder();
    /**
     * Get this key area.
     * @param iKeySeq The key area to set.
     * @return The key area.
     */
    public Key getKeyArea(int iKeySeq);
    /**
     * The number of key areas.
     * @return Key area count.
     */
    public int getKeyAreaCount();
    /**
     * Is this record a table (or a query stmt)?
     * This class is a table.
     * @return true If this is a queryrecord.
     */
    public boolean isQueryRecord();
    /**
     * Does this fieldlist have a primary key that is auto-sequence.
     * This can be turned off (in thick) by setting table.setProperty(AUTO_SEQUENCE_KEY, FALSE),
     * which is primarily used to restore an archive with changing the autosequenced values.
     * @return True if autosequence key.
     */
    public boolean isAutoSequence();
    /**
     * Does this fieldlist have a primary key that is auto-sequence.
     * This can be turned off (in thick) by setting table.setProperty(AUTO_SEQUENCE_KEY, FALSE),
     * which is primarily used to restore an archive with changing the autosequenced values.
     * In this is is okay to set this, although usually only set by system functions.
     * @param bIsAutoSequence True if autosequence key.
     * @return Old autosequence value.
     */
    public boolean setAutoSequence(boolean bIsAutoSequence);
    /**
     * Get the autosequence field if it exists.
     * @return The counterfield or null.
     */
    public Field getCounterField();
    /**
     * Refresh this record to the current data.
     * @param iChangeType Optional change type if a message was received saying this changed.
     * @param bWarningIfChanged If anything has changed in this record, return a RECORD_CHANGED warning.
     * @return error
     */
    public int refreshToCurrent(int iChangeType, boolean bWarningIfChanged);
}
