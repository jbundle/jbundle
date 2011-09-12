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
import org.jbundle.base.util.*;
import org.jbundle.model.*;

/**
 *  PropertiesRecord - A Record with a property field that is moved to and from
some virtual fields..
 */
public class PropertiesRecord extends VirtualRecord
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kProperties = kVirtualRecordLastField + 1;
    public static final int kPropertiesRecordLastField = kProperties;
    public static final int kPropertiesRecordFields = kProperties - DBConstants.MAIN_FIELD + 1;
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

    public static final String kPropertiesRecordFile = null;    // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        //if (iFieldSeq == kID)
        //{
        //  field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        if (iFieldSeq == kProperties)
            field = new PropertiesField(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kPropertiesRecordLastField)
                field = new EmptyField(this);
        }
        return field;
    }
    /**
     * Add the behaviors to sync this property to this virtual field.
     */
    public void addPropertiesFieldBehavior(BaseField fldDisplay, String strProperty)
    {
        BaseField fldProperties = this.getField(PropertiesRecord.kProperties);
        FieldListener listener = new CopyConvertersHandler(new PropertiesConverter(fldProperties, strProperty));
        listener.setRespondsToMode(DBConstants.INIT_MOVE, false);
        listener.setRespondsToMode(DBConstants.READ_MOVE, false);
        fldDisplay.addListener(listener);
        listener = new CopyConvertersHandler(fldDisplay, new PropertiesConverter(fldProperties, strProperty));
        fldProperties.addListener(listener);
    }

}
