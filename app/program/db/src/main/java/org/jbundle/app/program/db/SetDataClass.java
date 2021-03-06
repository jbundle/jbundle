/**
 * @(#)SetDataClass.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.db;

import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;

/**
 *  SetDataClass - Set the physical data class.
 */
public class SetDataClass extends FileListener
{
    /**
     * Default constructor.
     */
    public SetDataClass()
    {
        super();
    }
    /**
     * SetDataClass Method.
     */
    public SetDataClass(Record record)
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
        String strClass = this.getOwner().getField(FieldData.FIELD_CLASS).toString();
        String strType = null;
        if (strClass.indexOf("Field") != -1)
        {
            strType = strClass.substring(0, strClass.indexOf("Field"));
            if ("Short Integer Double Float Currencys Percent Real Boolean String DateTime".indexOf(strType) == -1)
                strType = null;
            if (strType != null) if ((strType.equals("DateTime")) || (strType.equals("Time")))
                strType = "Date";
            if (strType != null) if ((strType.equals("Currencys")) || (strType.equals("Real")))
                strType = "Double";
            if (strType != null) if (strType.equals("Percent"))
                strType = "Float";
        }
        if (strType != null)
            this.getOwner().getField(FieldData.DATA_CLASS).setString(strType);
        super.doValidRecord(bDisplayOption);
    }

}
