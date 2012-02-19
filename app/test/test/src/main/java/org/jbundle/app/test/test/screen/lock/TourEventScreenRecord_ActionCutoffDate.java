/*
 *  @(#)TourEventScreenRecord_ActionCutoffDate.
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.test.screen.lock;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.util.*;
import org.jbundle.thin.base.db.*;


/**
 *  TourEventScreenRecord_ActionCutoffDate - .
 */
public class TourEventScreenRecord_ActionCutoffDate extends DateField
{
    /**
     * Default constructor.
     */
    public TourEventScreenRecord_ActionCutoffDate()
    {
        super();
    }
    /**
     * TourEventScreenRecord_ActionCutoffDate Method.
     */
    public TourEventScreenRecord_ActionCutoffDate(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Initialize the field.
     */
    public int initField(boolean displayOption)
    {
        return this.setValue(todaysDate(), displayOption, DBConstants.INIT_MOVE);
    }

}
