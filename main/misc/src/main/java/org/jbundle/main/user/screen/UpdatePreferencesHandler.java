/**
 * @(#)UpdatePreferencesHandler.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.user.screen;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.main.user.db.*;

/**
 *  UpdatePreferencesHandler - .
 */
public class UpdatePreferencesHandler extends FileListener
{
    protected Map<String, Object> m_properties = null;
    /**
     * Default constructor.
     */
    public UpdatePreferencesHandler()
    {
        super();
    }
    /**
     * UpdatePreferencesHandler Method.
     */
    public UpdatePreferencesHandler(Record record)
    {
        this();
        this.init(record);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record)
    {
        super.init(record);
    }
    /**
     * Called when a valid record is read from the table/query.
     * @param bDisplayOption If true, display any changes.
     */
    public void doValidRecord(boolean bDisplayOption)
    {
        super.doValidRecord(bDisplayOption);
        m_properties = ((PropertiesField)this.getOwner().getField(UserInfo.PROPERTIES)).getProperties();
    }
    /**
     * Called when a change is the record status is about to happen/has happened.
     * @param field If this file change is due to a field, this is the field.
     * @param iChangeType The type of change that occurred.
     * @param bDisplayOption If true, display any changes.
     * @return an error code.
     * ADD_TYPE - Before a write.
     * UPDATE_TYPE - Before an update.
     * DELETE_TYPE - Before a delete.
     * AFTER_UPDATE_TYPE - After a write or update.
     * LOCK_TYPE - Before a lock.
     * SELECT_TYPE - After a select.
     * DESELECT_TYPE - After a deselect.
     * MOVE_NEXT_TYPE - After a move.
     * AFTER_REQUERY_TYPE - Record opened.
     * SELECT_EOF_TYPE - EOF Hit.
     */
    public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
    {
        if (iChangeType == DBConstants.AFTER_UPDATE_TYPE)
        {
            Map<String,Object> properties = ((PropertiesField)this.getOwner().getField(UserInfo.PROPERTIES)).getProperties();
            if (properties != null)
            {
                if (m_properties != null)
                {
                    for (String strProperty : m_properties.keySet())
                    {
                        Object objValue = m_properties.get(strProperty);
                        if (!objValue.equals(properties.get(strProperty)))
                        {
                            String strValue = null;
                            if (properties.get(strProperty) != null)
                                strValue = properties.get(strProperty).toString();
                            this.getOwner().getTask().getApplication().getSystemRecordOwner().setProperty(strProperty, strValue);
                            properties.remove(strProperty);
                        }
                    }                            
                }
                for (String strProperty : properties.keySet())
                {
                    String strValue = properties.get(strProperty).toString();
                    this.getOwner().getTask().getApplication().getSystemRecordOwner().setProperty(strProperty, strValue);
                }
            }
        }
        return super.doRecordChange(field, iChangeType, bDisplayOption);
    }

}
