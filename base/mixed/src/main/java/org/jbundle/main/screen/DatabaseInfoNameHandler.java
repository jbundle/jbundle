/**
 *  @(#)DatabaseInfoNameHandler.
 *  Copyright © 2010 tourapp.com. All rights reserved.
 */
package org.jbundle.main.screen;

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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.main.db.*;

/**
 *  DatabaseInfoNameHandler - .
 */
public class DatabaseInfoNameHandler extends FieldListener
{
    /**
     * Default constructor.
     */
    public DatabaseInfoNameHandler()
    {
        super();
    }
    /**
     * DatabaseInfoNameHandler Method.
     */
    public DatabaseInfoNameHandler(BaseField field)
    {
        this();
        this.init(field);
    }
    /**
     * Initialize class fields.
     */
    public void init(BaseField field)
    {
        super.init(field);
    }
    /**
     * FieldChanged Method.
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        if (iMoveMode == DBConstants.SCREEN_MOVE)
        {
            String strName = this.getOwner().toString();
            int iType = 0;
            iType = DBConstants.REMOTE | DBConstants.SHARED_DATA;
            DatabaseInfo record = (DatabaseInfo)this.getOwner().getRecord();
            if (strName.length() == 0)
            {
                
            }
            else
            {
                record.close();
                BaseTable table = record.getTable();
                record.setTable(null);
                table.setRecord(null);
                table.free();
                record.setDatabaseName(strName);
                if (iType != 0)
                    record.setDatabaseType(iType);
                try {
                    record.open();
                    record.setKeyArea(DatabaseInfo.kIDKey);
                    record.getField(DatabaseInfo.kID).setValue(1);
                    if (!record.seek(DBConstants.EQUALS))
                    {
                        record.addNew();
                        this.getOwner().setString(strName);
                    }
                } catch (DBException e) {
                    e.printStackTrace();
                }
            }
            
        }
        return super.fieldChanged(bDisplayOption, iMoveMode);
    }

}
