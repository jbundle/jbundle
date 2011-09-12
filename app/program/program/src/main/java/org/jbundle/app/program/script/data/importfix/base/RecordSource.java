/**
 * @(#)RecordSource.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.data.importfix.base;

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
 *  RecordSource - .
 */
public class RecordSource extends Object
     implements Iterator<Record>
{
    protected Record m_record;
    /**
     * Default constructor.
     */
    public RecordSource()
    {
        super();
    }
    /**
     * RecordSource Method.
     */
    public RecordSource(Record record)
    {
        this();
        this.init(record);
    }
    /**
     * Init Method.
     */
    public void init(Record record)
    {
        m_record = record;
    }
    /**
     * HasNext Method.
     */
    public boolean hasNext()
    {
        try {
            return m_record.hasNext();
        } catch (DBException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    /**
     * Next Method.
     */
    public Record next()
    {
        try {
            return m_record.next();
        } catch (DBException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    /**
     * Remove Method.
     */
    public void remove()
    {
        // Not used
    }

}
