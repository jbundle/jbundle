/**
 * @(#)BaseSource.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.data.importfix.base;

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
import org.jbundle.base.screen.model.*;
import java.io.*;

/**
 *  BaseSource - .
 */
public class BaseSource extends Object
     implements Iterator<Record>
{
    protected Reader m_reader = null;
    protected Record m_record = null;
    /**
     * Default constructor.
     */
    public BaseSource()
    {
        super();
    }
    /**
     * BaseSource Method.
     */
    public BaseSource(Reader reader, Record record)
    {
        this();
        this.init(reader, record);
    }
    /**
     * Init Method.
     */
    public void init(Reader reader, Record record)
    {
        m_reader = reader;
        m_record = record;
    }
    /**
     * HasNext Method.
     */
    public boolean hasNext()
    {
        try {
            if (!this.parseNextLine())
            {
                try {
                    m_record.addNew();
                } catch (DBException e) {
                    e.printStackTrace();
                }
                m_record.close();
                m_reader.close();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return true;
    }
    /**
     * Parse the next line and return false at EOF.
     */
    public boolean parseNextLine()
    {
        return false;   // EOF // Override this
    }
    /**
     * Next Method.
     */
    public Record next()
    {
        return this.moveDataToRecord();
    }
    /**
     * MoveDataToRecord Method.
     */
    public Record moveDataToRecord()
    {
        return null;    // Override this
    }
    /**
     * Remove Method.
     */
    public void remove()
    {
        // Not used
    }

}
