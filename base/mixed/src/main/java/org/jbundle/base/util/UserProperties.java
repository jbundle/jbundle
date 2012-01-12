/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.util;

/**
 * @(#)STEView.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.BaseField;
import org.jbundle.base.field.PropertiesField;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.model.main.user.db.UserRegistrationModel;
import org.jbundle.model.DBException;
import org.jbundle.model.PropertyOwner;
import org.jbundle.thin.base.db.Constants;


/**
 * UserProperties - Wrapper for the registration database.
 * UserProperties are used to store screen-specific properties such as default field values
 * and screen size.
 * <p/>You use this class by calling retrieveUserProperties(strRegistrationKey) from
 * any propertyOwner. If you pass a null as the registrationKey, you will get the application
 * as the propertyowner, where you can then call setProperty(xxx) to save system-wide
 * parameters on the current user.
 * <br/>If you pass a registration key, you will get a new or current UserProperties object
 * and you will be able to setProperty(xxx) to save screen-specific values such as field
 * initial values.
 * NOTE: You must be VERY careful when you are using a UserProperties object and free it
 * when you are done, so the registration information can be saved.
 * The physical data is stored in the UserRegistration table.
 */
public class UserProperties extends Object
    implements PropertyOwner
{
    /**
     * My parent application.
     */
    protected MainApplication m_app = null;
    /**
     * This registration item's key.
     */
    protected String m_strKey = null;
    /**
     * The number of programs currently accessing this. Typically this is set
     * when the user has the same screen open twice.
     */
    protected int m_iUseCount = 0;

    /**
     * Constructor.
     */
    public UserProperties()
    {
        super();
    }
    /**
     * Constructor.
     * @param app The parent application for these properties.
     * @param strKey The lookup key for these properties.
     */
    public UserProperties(MainApplication app, String strKey)
    {
        this();
        this.init(app, strKey);
    }
    /**
     * Constructor.
     * @param app The parent application for these properties.
     * @param strKey The lookup key for these properties.
     */
    public void init(MainApplication app, String strKey)
    {
        m_app = app;
        if (strKey == null)
            strKey = Constants.BLANK;
        m_strKey = strKey;  // + "\\";
        m_app.addUserProperties(this);
    }
    /**
     * Free this resource.
     */
    public void free()
    {
        if (this.bumpUseCount(-1) <= 0)
        {   // Only free when no one is using this anymore
            m_app.removeUserProperties(this);
            m_strKey = null;
            this.getUserRegistration();     // This will flush any current value
        }
    }
    /**
     * Get the registration key for this resource.
     * @return The lookup key for this resource.
     */
    public String getKey()
    {
        return m_strKey;
    }
    /**
     * Get the use count.
     * @param iIncrement The amount to increase or decrease the use count by.
     * @return The new use count.
     */
    public int bumpUseCount(int iIncrement)
    {
        m_iUseCount = m_iUseCount + iIncrement;
        return m_iUseCount;
    }
    /**
     * Gets the user registration record for this key (which is opened by my parent, the application).
     * The first time through, this method reads or adds a new entry.
     * On subsequent uses, I read the current registration entry.
     * This method is also called from free (which sets the key to null, then calls) which flushes the record.
     * @return The User registration record.
     */
    public Record getUserRegistration()
    {
        Record recUserRegistration = m_app.getUserRegistration();

        if (((recUserRegistration.getEditMode() == Constants.EDIT_CURRENT) ||
            (recUserRegistration.getEditMode() == Constants.EDIT_IN_PROGRESS))
            && ((m_app.getUserID() != null) && (!recUserRegistration.getField(UserRegistrationModel.USER_ID).isNull())
                    && (m_app.getUserID().equals(recUserRegistration.getField(UserRegistrationModel.USER_ID).toString())) &&
                (recUserRegistration.getField(UserRegistrationModel.CODE).toString().equalsIgnoreCase(m_strKey))))
        { // Key is current... no need to read
        }
        else
        {
            if (recUserRegistration.getEditMode() != Constants.EDIT_NONE)
                if (recUserRegistration.isModified(false))
            { // flush the current property (if changed)
                try   {
                    if (recUserRegistration.getEditMode() == Constants.EDIT_ADD)
                        recUserRegistration.add();
                    else
                    {
                        if (recUserRegistration.getEditMode() == Constants.EDIT_CURRENT)
                            recUserRegistration.edit();
                        recUserRegistration.set();
                    }
                } catch (DBException ex)    {
                    ex.printStackTrace();
                }
            }
    
            if (m_strKey == null)
                return null;
            ((ReferenceField)recUserRegistration.getField(UserRegistrationModel.USER_ID)).setString(m_app.getUserID());
            recUserRegistration.getField(UserRegistrationModel.CODE).setString(m_strKey);
            recUserRegistration.setKeyArea(UserRegistrationModel.USER_ID_KEY);
            try   {
                boolean bFound = recUserRegistration.seek("=");
                if (!bFound)
                {   // Key not found, add a new one
                    recUserRegistration.addNew();
                    ((ReferenceField)recUserRegistration.getField(UserRegistrationModel.USER_ID)).setString(m_app.getUserID());
                    recUserRegistration.getField(UserRegistrationModel.CODE).setString(m_strKey);
                }
            } catch (DBException ex)    {
                ex.printStackTrace();
            }
        }
        return recUserRegistration;
    }
    /**
     * Get the value associated with this key.
     * @param strName The key to lookup.
     * @return The value for this sub-key.
     */
    public String getProperty(String strName)
    {
        Record recUserRegistration = this.getUserRegistration();
        return ((PropertiesField)recUserRegistration.getField(UserRegistrationModel.PROPERTIES)).getProperty(strName);
    }
    /**
     * Set this property.
     * @param strProperty The property key.
     * @param strValue The property value.
     */
    public void setProperty(String strName, String strData)
    {
        Record recUserRegistration = this.getUserRegistration();
        ((PropertiesField)recUserRegistration.getField(UserRegistrationModel.PROPERTIES)).setProperty(strName, strData);
    }
    /**
     * Set the properties.
     * @param strProperties The properties to set.
     */
    public void setProperties(Map<String, Object> properties)
    {
        Record recUserRegistration = this.getUserRegistration();
        ((PropertiesField)recUserRegistration.getField(UserRegistrationModel.PROPERTIES)).setProperties(properties);
    }
    /**
     * Set the properties.
     * @param strProperties The properties to set.
     */
    public Map<String, Object> getProperties()
    {
        Record recUserRegistration = this.getUserRegistration();
        return ((PropertiesField)recUserRegistration.getField(UserRegistrationModel.PROPERTIES)).getProperties();
    }
    /**
     * Retrieve/Create a user properties record with this lookup key.
     * Of course this retrieves this object.
     * @param strPropertyCode The key I'm looking up.
     * @return The UserProperties for this registration key.
     */
    public PropertyOwner retrieveUserProperties(String strRegistrationKey)
    {
        return this;        // This is the last owner in the chain
    }
    /**
     * Set this field to the value save under the field's name.
     * (Utility method).
     * @param field Field to retrieve.
     * @return true If the value was retrieved from the registration database.
     */
    public boolean retrieveField(BaseField field)
    {
        String strFieldName = field.getFieldName();   // Fieldname only
        String strData = (String)this.getProperty(strFieldName);
        boolean bFound = false;
        if (strData != null)
        {
            field.setString(strData, DBConstants.DISPLAY, DBConstants.INIT_MOVE);
            bFound = true;
        }
        return bFound;
    }
    /**
     * Set a property with this field's name as the key to this field's current value.
     * (Utility method).
     * @param field The field to save.
     */
    public void saveField(BaseField field)
    {
        String strFieldName = field.getFieldName();   // Fieldname only
        String strData = field.getString();
        this.setProperty(strFieldName, strData);
    }
}
