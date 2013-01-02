/**
 * @(#)CheckNonNumericListener.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.db;

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
import org.jbundle.thin.base.screen.*;

/**
 *  CheckNonNumericListener - Check to make sure this field is not numeric.
 */
public class CheckNonNumericListener extends FieldListener
{
    /**
     * Default constructor.
     */
    public CheckNonNumericListener()
    {
        super();
    }
    /**
     * CheckNonNumericListener Method.
     */
    public CheckNonNumericListener(BaseField field)
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
     * Field must be non-numeric.
     */
    public int fieldChanged(boolean bDisplayOption, int iMoveMode)
    {
        String string = this.getOwner().toString();
        if (Utility.isNumeric(string))
        {
            Task task = null;
            if (this.getOwner() != null)
                if (this.getOwner().getRecord() != null)
                    if (this.getOwner().getRecord().getRecordOwner() != null)
                        task = this.getOwner().getRecord().getRecordOwner().getTask();
            if (task == null)
                task = BaseApplet.getSharedInstance();
            return task.setLastError("Must be non-numeric");
        }
        return super.fieldChanged(bDisplayOption, iMoveMode);
    }

}
