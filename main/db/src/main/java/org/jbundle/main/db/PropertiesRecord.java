/**
 * @(#)PropertiesRecord.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.db;

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
import org.jbundle.model.main.db.*;

/**
 *  PropertiesRecord - A Record with a property field that is moved to and from
some virtual fields..
 */
public class PropertiesRecord extends VirtualRecord
     implements PropertiesRecordModel
{
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public PropertiesRecord()
    {
        super();
    }
    /**
     * Constructor.
     */
    public PropertiesRecord(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwner screen)
    {
        super.init(screen);
    }

    public static final String PROPERTIES_RECORD_FILE = null; // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        //if (iFieldSeq == 0)
        //{
        //  field = new CounterField(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 1)
        //{
        //  field = new RecordChangedField(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 2)
        //{
        //  field = new BooleanField(this, DELETED, Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        //  field.setHidden(true);
        //}
        if (iFieldSeq == 3)
            field = new PropertiesField(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }
    /**
     * Add the behaviors to sync this property to this virtual field.
     */
    public void addPropertiesFieldBehavior(BaseField fldDisplay, String strProperty)
    {
        BaseField fldProperties = this.getField(PropertiesRecord.PROPERTIES);
        FieldListener listener = new CopyConvertersHandler(new PropertiesConverter(fldProperties, strProperty));
        listener.setRespondsToMode(DBConstants.INIT_MOVE, false);
        listener.setRespondsToMode(DBConstants.READ_MOVE, false);
        fldDisplay.addListener(listener);
        listener = new CopyConvertersHandler(fldDisplay, new PropertiesConverter(fldProperties, strProperty));
        fldProperties.addListener(listener);
    }

}
